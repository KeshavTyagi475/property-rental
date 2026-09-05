import { useEffect, useState } from "react";
import { Archive, Plus, RotateCcw } from "lucide-react";
import {
  archiveUnit,
  createUnit,
  getUnits,
  restoreUnit,
  updateUnit,
  type Unit,
} from "../api/units";
import { useNavigate } from "react-router-dom";

export default function UnitsPage() {
  const [units, setUnits] = useState<Unit[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [editingUnitId, setEditingUnitId] = useState<number | null>(null);
  const [unitNumber, setUnitNumber] = useState("");
  const [address, setAddress] = useState("");
  const [monthlyRent, setMonthlyRent] = useState("");
  const [currentTenant, setCurrentTenant] = useState("");
  const [saving, setSaving] = useState(false);
  const navigate = useNavigate();
 

  async function loadUnits() {
    try {
      setError("");
      const data = await getUnits();
      setUnits(data);
    } catch {
      setError("Unable to load units.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadUnits();
  }, []);
  
  function handleEdit(unit: Unit) {
    setEditingUnitId(unit.id);
    setUnitNumber(unit.unitNumber);
    setAddress(unit.address);
    setMonthlyRent(String(unit.monthlyRent));
    setCurrentTenant(unit.currentTenant ?? "");
    setShowForm(true);
    setError("");
  }
  
  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setError("");

    try {
      if (editingUnitId !== null) {
        const updated = await updateUnit(editingUnitId, {
          unitNumber,
          address,
          monthlyRent: Number(monthlyRent),
          currentTenant: currentTenant || null,
        });

        setUnits((current) =>
          current.map((unit) =>
            unit.id === editingUnitId ? updated : unit,
          ),
        );
      } else {
        const unit = await createUnit({
          unitNumber,
          address,
          monthlyRent: Number(monthlyRent),
          currentTenant: currentTenant || null,
        });

        setUnits((current) => [...current, unit]);
      }

      setUnitNumber("");
      setAddress("");
      setMonthlyRent("");
      setCurrentTenant("");
      setEditingUnitId(null);
      setShowForm(false);
    } catch {
      setError(
        editingUnitId !== null
          ? "Unable to update unit."
          : "Unable to create unit.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleArchive(id: number) {
    try {
      const updated = await archiveUnit(id);

      setUnits((current) =>
        current.map((unit) => (unit.id === id ? updated : unit)),
      );
    } catch {
      setError("Unable to archive unit.");
    }
  }

  async function handleRestore(id: number) {
    try {
      const updated = await restoreUnit(id);

      setUnits((current) =>
        current.map((unit) => (unit.id === id ? updated : unit)),
      );
    } catch {
      setError("Unable to restore unit.");
    }
  }

  if (loading) {
    return <p className="text-sm text-gray-500">Loading units...</p>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Units</h1>
          <p className="mt-1 text-sm text-gray-500">
            Manage rental units and their current tenants.
          </p>
        </div>

        <button
          type="button"
          onClick={() => setShowForm((current) => !current)}
          className="flex items-center gap-2 rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800"
        >
          <Plus size={18} />
          Add Unit
        </button>
      </div>

      {error && (
        <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {showForm && (
		<form
		  onSubmit={handleSubmit}
          className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm"
        >
		<h2 className="text-lg font-semibold text-gray-900">
		  {editingUnitId !== null ? "Edit Unit" : "Create Unit"}
		</h2>

          <div className="mt-4 grid gap-4 md:grid-cols-2">
            <input
              value={unitNumber}
              onChange={(event) => setUnitNumber(event.target.value)}
              placeholder="Unit number"
              required
              className="rounded-lg border border-gray-300 px-3 py-2"
            />

            <input
              value={address}
              onChange={(event) => setAddress(event.target.value)}
              placeholder="Address"
              required
              className="rounded-lg border border-gray-300 px-3 py-2"
            />

            <input
              value={monthlyRent}
              onChange={(event) => setMonthlyRent(event.target.value)}
              placeholder="Monthly rent"
              type="number"
              min="0.01"
              step="0.01"
              required
              className="rounded-lg border border-gray-300 px-3 py-2"
            />

            <input
              value={currentTenant}
              onChange={(event) => setCurrentTenant(event.target.value)}
              placeholder="Current tenant"
              className="rounded-lg border border-gray-300 px-3 py-2"
            />
          </div>

          <div className="mt-5 flex gap-3">
            <button
              type="submit"
              disabled={saving}
              className="rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
            >
			{saving
			  ? "Saving..."
			  : editingUnitId !== null
			    ? "Update Unit"
			    : "Create Unit"}
            </button>

            <button
              type="button"
              onClick={() => setShowForm(false)}
              className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700"
            >
              Cancel
            </button>
          </div>
        </form>
      )}

      <div className="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                Unit
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                Address
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                Monthly Rent
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                Tenant
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                Status
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium uppercase text-gray-500">
                Action
              </th>
            </tr>
          </thead>

          <tbody className="divide-y divide-gray-200">
            {units.map((unit) => (
              <tr key={unit.id}>
			  <td className="px-6 py-4">
			    <button
			      type="button"
			      onClick={() => navigate(`/units/${unit.id}`)}
			      className="font-medium text-blue-600 hover:underline"
			    >
			      {unit.unitNumber}
			    </button>
			  </td>

                <td className="px-6 py-4 text-sm text-gray-600">
                  {unit.address}
                </td>

                <td className="px-6 py-4 text-sm text-gray-600">
                  ₹{unit.monthlyRent.toLocaleString()}
                </td>

                <td className="px-6 py-4 text-sm text-gray-600">
                  {unit.currentTenant || "—"}
                </td>

                <td className="px-6 py-4 text-sm">
                  {unit.archived ? (
                    <span className="rounded-full bg-gray-100 px-2 py-1 text-xs font-medium text-gray-600">
                      Archived
                    </span>
                  ) : (
                    <span className="rounded-full bg-green-100 px-2 py-1 text-xs font-medium text-green-700">
                      Active
                    </span>
                  )}
                </td>
				
				<td className="px-6 py-4 text-right">
					
					<button
				  		type="button"
				  		onClick={() => navigate(`/units/${unit.id}`)}
				  		className="mr-2 inline-flex items-center rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-700 hover:bg-gray-50"
						>
				  		View
					</button>
				  <button
				    type="button"
				    onClick={() => handleEdit(unit)}
				    className="mr-2 inline-flex items-center rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-700 hover:bg-gray-50"
				  >
				    Edit
				  </button>

				  {unit.archived ? (
				    <button
				      type="button"
				      onClick={() => handleRestore(unit.id)}
				      className="inline-flex items-center gap-2 rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-700 hover:bg-gray-50"
				    >
				      <RotateCcw size={16} />
				      Restore
				    </button>
				  ) : (
				    <button
				      type="button"
				      onClick={() => handleArchive(unit.id)}
				      className="inline-flex items-center gap-2 rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-700 hover:bg-gray-50"
				    >
				      <Archive size={16} />
				      Archive
				    </button>
				  )}
				</td>
                
              </tr>
            ))}

            {units.length === 0 && (
              <tr>
                <td
                  colSpan={6}
                  className="px-6 py-8 text-center text-sm text-gray-500"
                >
                  No units found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}