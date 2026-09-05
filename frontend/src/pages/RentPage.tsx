import { useEffect, useState } from "react";
import { getUnits, type Unit } from "../api/units";
import {
  getRentPaymentHistory,
  recordRentPayment,
  getRentRoll,
  recordBulkRentPayments,
  type RentPayment,
  type BulkRentPaymentResponse,
} from "../api/rent";

export default function RentPage() {
  const [units, setUnits] = useState<Unit[]>([]);
  const [selectedUnitId, setSelectedUnitId] = useState("");
  const [paymentMonth, setPaymentMonth] = useState(
    new Date().toISOString().slice(0, 7)
  );
  const [amount, setAmount] = useState("");
  const [payments, setPayments] = useState<RentPayment[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [rentRoll, setRentRoll] = useState<string>("");
  const [rentRollLoading, setRentRollLoading] = useState(false);
  const [bulkPayments, setBulkPayments] =
    useState<Record<number, string>>({});
  const [bulkLoading, setBulkLoading] = useState(false);
  const [bulkResult, setBulkResult] =
    useState<BulkRentPaymentResponse | null>(null);

  useEffect(() => {
    getUnits()
      .then(setUnits)
      .catch(() => setError("Unable to load units."))
      .finally(() => setLoading(false));
  }, []);

  async function loadPaymentHistory(unitId: number) {
    try {
      const result = await getRentPaymentHistory(unitId);
      setPayments(result);
    } catch {
      setPayments([]);
    }
  }

  async function handleUnitChange(unitId: string) {
    setSelectedUnitId(unitId);
    setPayments([]);
    setError("");
    setSuccess("");

    if (unitId) {
      await loadPaymentHistory(Number(unitId));
    }
  }

  async function handleRecordPayment() {
    if (!selectedUnitId || !paymentMonth || !amount) {
      setError("Unit, payment month, and amount are required.");
      return;
    }

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await recordRentPayment(Number(selectedUnitId), {
        paymentMonth: `${paymentMonth}-01`,
        amount: Number(amount),
      });

      await loadPaymentHistory(Number(selectedUnitId));

      setAmount("");
      setSuccess("Rent payment recorded successfully.");
    } catch {
      setError("Unable to record rent payment.");
    } finally {
      setSaving(false);
    }
  }
  async function handleLoadRentRoll() {
    setRentRollLoading(true);
    setError("");

    try {
      const result = await getRentRoll(`${paymentMonth}-01`);
      setRentRoll(result);
    } catch {
      setError("Unable to load rent roll.");
    } finally {
      setRentRollLoading(false);
    }
  }
  
  async function handleBulkRent() {
	const entries = Object.entries(bulkPayments)
	  .filter(([, value]) => value.trim() !== "")
	  .map(([unitId, value]) => {
	    const unit = units.find((item) => item.id === Number(unitId));

	    return {
	      unitNumber: unit?.unitNumber ?? "",
	      amount: Number(value),
	    };
	  });

    if (!paymentMonth) {
      setError("Payment month is required.");
      return;
    }

    if (entries.length === 0) {
      setError("Enter at least one rent payment.");
      return;
    }

    if (entries.some((payment) => payment.amount <= 0)) {
      setError("All payment amounts must be greater than zero.");
      return;
    }

    setBulkLoading(true);
    setError("");
    setSuccess("");
    setBulkResult(null);

    try {
      const result = await recordBulkRentPayments({
        paymentMonth: `${paymentMonth}-01`,
        payments: entries,
      });

      setBulkResult(result);
      setSuccess("Bulk rent payments recorded successfully.");
    } catch {
      setError("Unable to record bulk rent payments.");
    } finally {
      setBulkLoading(false);
    }
  }
  
  if (loading) {
    return <div className="text-gray-600">Loading rent...</div>;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Rent</h1>
        <p className="mt-1 text-gray-500">
          Record payments, process bulk rent, and view the rent roll.
        </p>
      </div>

      {error && (
        <div className="rounded-lg bg-red-50 p-4 text-sm text-red-700">
          {error}
        </div>
      )}

      {success && (
        <div className="rounded-lg bg-green-50 p-4 text-sm text-green-700">
          {success}
        </div>
      )}

      <div className="rounded-xl bg-white p-6 shadow-sm">
        <h2 className="text-lg font-semibold text-gray-900">
          Record Rent Payment
        </h2>

        <div className="mt-4 grid gap-4 md:grid-cols-3">
          <select
            value={selectedUnitId}
            onChange={(event) => handleUnitChange(event.target.value)}
            className="rounded-lg border border-gray-300 px-4 py-2 text-sm"
          >
            <option value="">Select Unit</option>

            {units
              .filter((unit) => !unit.archived)
              .map((unit) => (
                <option key={unit.id} value={unit.id}>
                  Unit {unit.unitNumber}
                </option>
              ))}
          </select>

          <input
            type="month"
            value={paymentMonth}
            onChange={(event) => setPaymentMonth(event.target.value)}
            className="rounded-lg border border-gray-300 px-4 py-2 text-sm"
          />

          <input
            type="number"
            min="0.01"
            step="0.01"
            value={amount}
            onChange={(event) => setAmount(event.target.value)}
            placeholder="Amount"
            className="rounded-lg border border-gray-300 px-4 py-2 text-sm"
          />
        </div>

        <button
          type="button"
          onClick={handleRecordPayment}
          disabled={saving}
          className="mt-4 rounded-lg bg-gray-900 px-5 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:opacity-50"
        >
          {saving ? "Recording..." : "Record Payment"}
        </button>
      </div>

      {selectedUnitId && (
        <div className="rounded-xl bg-white p-6 shadow-sm">
          <h2 className="text-lg font-semibold text-gray-900">
            Payment History
          </h2>
		  
		  
          {payments.length === 0 ? (
            <p className="mt-4 text-sm text-gray-500">
              No payments recorded for this unit.
            </p>
          ) : (
            <div className="mt-4 overflow-hidden rounded-lg border border-gray-200">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
                      Month
                    </th>
                    <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
                      Amount
                    </th>
                    <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
                      Recorded
                    </th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-gray-200">
                  {payments.map((payment) => (
                    <tr key={payment.id}>
                      <td className="px-4 py-3 text-sm text-gray-700">
                        {payment.paymentMonth}
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-700">
                        ₹{Number(payment.amount).toFixed(2)}
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-700">
                        {new Date(payment.createdAt).toLocaleString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
		
      )}
	  <div className="rounded-xl bg-white p-6 shadow-sm">
	    <div>
	      <h2 className="text-lg font-semibold text-gray-900">
	        Bulk Rent Recording
	      </h2>
	      <p className="mt-1 text-sm text-gray-500">
	        Enter the payment amount for each unit for the selected month.
	      </p>
	    </div>

	    <div className="mt-4">
	      <label className="block text-sm font-medium text-gray-700">
	        Payment Month
	      </label>

	      <input
	        type="month"
	        value={paymentMonth}
	        onChange={(event) => setPaymentMonth(event.target.value)}
	        className="mt-1 rounded-lg border border-gray-300 px-4 py-2 text-sm"
	      />
	    </div>

	    <div className="mt-6 overflow-hidden rounded-lg border border-gray-200">
	      <table className="min-w-full divide-y divide-gray-200">
	        <thead className="bg-gray-50">
	          <tr>
	            <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
	              Unit
	            </th>
	            <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
	              Monthly Rent
	            </th>
	            <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
	              Payment Amount
	            </th>
	          </tr>
	        </thead>

	        <tbody className="divide-y divide-gray-200">
	          {units
	            .filter((unit) => !unit.archived)
	            .map((unit) => (
	              <tr key={unit.id}>
	                <td className="px-4 py-3 text-sm font-medium text-gray-900">
	                  Unit {unit.unitNumber}
	                </td>

	                <td className="px-4 py-3 text-sm text-gray-700">
	                  ₹{Number(unit.monthlyRent).toFixed(2)}
	                </td>

	                <td className="px-4 py-3">
	                  <input
	                    type="number"
	                    min="0"
	                    step="0.01"
	                    value={bulkPayments[unit.id] ?? ""}
	                    onChange={(event) =>
	                      setBulkPayments((current) => ({
	                        ...current,
	                        [unit.id]: event.target.value,
	                      }))
	                    }
	                    placeholder="Amount"
	                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm"
	                  />
	                </td>
	              </tr>
	            ))}
	        </tbody>
	      </table>
	    </div>

	    <button
	      type="button"
	      onClick={handleBulkRent}
	      disabled={bulkLoading}
	      className="mt-4 rounded-lg bg-gray-900 px-5 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:opacity-50"
	    >
	      {bulkLoading ? "Recording..." : "Record Bulk Rent"}
	    </button>

	    {bulkResult && (
	      <div className="mt-6 overflow-hidden rounded-lg border border-gray-200">
	        <table className="min-w-full divide-y divide-gray-200">
	          <thead className="bg-gray-50">
	            <tr>
	              <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
	                Unit
	              </th>
	              <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
	                Expected
	              </th>
	              <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
	                Paid
	              </th>
	              <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">
	                Status
	              </th>
	            </tr>
	          </thead>

	          <tbody className="divide-y divide-gray-200">
	            {bulkResult.results.map((result) => (
	              <tr key={result.unitId}>
	                <td className="px-4 py-3 text-sm font-medium text-gray-900">
	                  Unit {result.unitNumber}
	                </td>

	                <td className="px-4 py-3 text-sm text-gray-700">
	                  ₹{Number(result.expectedAmount).toFixed(2)}
	                </td>

	                <td className="px-4 py-3 text-sm text-gray-700">
	                  {result.paymentAmount == null
	                    ? "—"
	                    : `₹${Number(result.paymentAmount).toFixed(2)}`}
	                </td>

	                <td className="px-4 py-3 text-sm font-medium text-gray-700">
	                  {result.status}
	                </td>
	              </tr>
	            ))}
	          </tbody>
	        </table>
	      </div>
	    )}
	  </div>
	  <div className="rounded-xl bg-white p-6 shadow-sm">
	    <div className="flex items-center justify-between">
	      <div>
	        <h2 className="text-lg font-semibold text-gray-900">
	          Rent Roll
	        </h2>
	        <p className="mt-1 text-sm text-gray-500">
	          Monthly payment status for every unit.
	        </p>
	      </div>

	      <button
	        type="button"
	        onClick={handleLoadRentRoll}
	        disabled={rentRollLoading}
	        className="rounded-lg bg-gray-900 px-5 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:opacity-50"
	      >
	        {rentRollLoading ? "Loading..." : "Load Rent Roll"}
	      </button>
	    </div>

	    {rentRoll && (
	      <pre className="mt-4 overflow-x-auto rounded-lg border border-gray-200 bg-gray-50 p-4 text-sm text-gray-700">
	        {rentRoll}
	      </pre>
	    )}
	  </div>
	  		  
    </div>
	
  );
}