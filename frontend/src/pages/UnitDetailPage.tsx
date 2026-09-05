import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import {
  getUnit,
  getUnitMaintenance,
  type Unit,
  type UnitMaintenanceRequest,
} from "../api/units";

export default function UnitDetailPage() {
  const { unitId } = useParams();
  const navigate = useNavigate();
  const [maintenanceRequests, setMaintenanceRequests] = useState<
    UnitMaintenanceRequest[]
  >([]);

  const [unit, setUnit] = useState<Unit | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!unitId) return;

    const id = Number(unitId);

    Promise.all([getUnit(id), getUnitMaintenance(id)])
      .then(([unitData, maintenanceData]) => {
        setUnit(unitData);
        setMaintenanceRequests(maintenanceData);
      })
      .catch(() => setError("Unable to load unit."))
      .finally(() => setLoading(false));
  }, [unitId]);

  if (loading) {
    return <div className="text-gray-600">Loading unit...</div>;
  }

  if (error || !unit) {
    return (
      <div>
        <p className="text-red-600">{error || "Unit not found."}</p>
        <button
          type="button"
          onClick={() => navigate("/units")}
          className="mt-4 rounded-lg border border-gray-300 px-4 py-2 text-sm"
        >
          Back to Units
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <button
        type="button"
        onClick={() => navigate("/units")}
        className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900"
      >
        <ArrowLeft size={16} />
        Back to Units
      </button>

      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          Unit {unit.unitNumber}
        </h1>
        <p className="mt-1 text-gray-500">{unit.address}</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <div className="rounded-xl bg-white p-5 shadow-sm">
          <p className="text-sm text-gray-500">Monthly Rent</p>
          <p className="mt-2 text-xl font-semibold">
            ₹{unit.monthlyRent.toLocaleString()}
          </p>
        </div>

        <div className="rounded-xl bg-white p-5 shadow-sm">
          <p className="text-sm text-gray-500">Current Tenant</p>
          <p className="mt-2 text-xl font-semibold">
            {unit.currentTenant || "Vacant"}
          </p>
        </div>

        <div className="rounded-xl bg-white p-5 shadow-sm">
          <p className="text-sm text-gray-500">Status</p>
          <p className="mt-2 text-xl font-semibold">
            {unit.archived ? "Archived" : "Active"}
          </p>
        </div>
      </div>

	  <div className="rounded-xl bg-white p-6 shadow-sm">
	    <h2 className="text-lg font-semibold text-gray-900">
	      Maintenance Requests
	    </h2>

	    {maintenanceRequests.length === 0 ? (
	      <p className="mt-4 text-sm text-gray-500">
	        No maintenance requests for this unit.
	      </p>
	    ) : (
	      <div className="mt-4 space-y-3">
	        {maintenanceRequests.map((request) => (
	          <div
	            key={request.id}
	            className="rounded-lg border border-gray-200 p-4"
	          >
	            <div className="flex items-center justify-between">
	              <p className="font-medium text-gray-900">
	                Request #{request.id}
	              </p>

	              <span className="rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-700">
	                {request.status}
	              </span>
	            </div>

	            <p className="mt-2 text-sm text-gray-700">
	              {request.description}
	            </p>

	            <div className="mt-3 flex gap-4 text-xs text-gray-500">
	              <span>Priority: {request.priority}</span>
	              <span>
	                Created by: {request.createdByUsername}
	              </span>
	            </div>
	          </div>
	        ))}
	      </div>
	    )}
	  </div>
    </div>
  );
}