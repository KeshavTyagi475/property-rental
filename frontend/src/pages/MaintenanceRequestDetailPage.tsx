import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { useAuth } from "../auth/AuthContext";
import {
  getMaintenanceRequest,
  updateMaintenanceRequest,
  getMaintenanceTimeline,
  addMaintenanceNote,
  updateMaintenanceStatus,
  getContractors,
  assignContractor,
  unassignContractor,
  type Contractor,
  type Status,
  type MaintenanceTimeline,
  type MaintenanceRequest,
  type Priority,
} from "../api/maintenance";

export default function MaintenanceRequestDetailPage() {
  const { requestId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const isManager = user?.roles.includes("ROLE_PROPERTY_MANAGER");
  const [timeline, setTimeline] = useState<MaintenanceTimeline[]>([]);
  const [timelineLoading, setTimelineLoading] = useState(true);
  const [request, setRequest] = useState<MaintenanceRequest | null>(null);
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState<Priority>("MEDIUM");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [note, setNote] = useState("");
  const [addingNote, setAddingNote] = useState(false);
  const [status, setStatus] = useState<Status>("REPORTED");
  const [updatingStatus, setUpdatingStatus] = useState(false);
  const [contractors, setContractors] = useState<Contractor[]>([]);
  const [selectedContractorId, setSelectedContractorId] = useState("");
  const [assigningContractor, setAssigningContractor] = useState(false);

  useEffect(() => {
    if (!requestId) return;

    const id = Number(requestId);

    getMaintenanceRequest(id)
      .then((data) => {
        setRequest(data);
        setDescription(data.description);
        setPriority(data.priority);
		setStatus(data.status);
      })
      .catch(() => setError("Failed to load maintenance request."))
      .finally(() => setLoading(false));

    getMaintenanceTimeline(id)
      .then(setTimeline)
      .catch(() => setTimeline([]))
      .finally(() => setTimelineLoading(false));
	  
	getContractors()
	  .then(setContractors)
	  .catch(() => setContractors([]));
  }, [requestId]);

  async function handleSave() {
    if (!requestId) return;

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await updateMaintenanceRequest(
        Number(requestId),
        {
          description,
          priority,
        }
      );

      setRequest(updated);
      setDescription(updated.description);
      setPriority(updated.priority);
      setSuccess("Maintenance request updated successfully.");
    } catch {
      setError("Failed to update maintenance request.");
    } finally {
      setSaving(false);
    }
  }
  
  async function handleAddNote() {
    if (!requestId || !note.trim()) return;

    setAddingNote(true);
    setError("");
    setSuccess("");

    try {
      const newEvent = await addMaintenanceNote(Number(requestId), {
        note: note.trim(),
      });

      setTimeline((current) => [...current, newEvent]);
      setNote("");
      setSuccess("Note added successfully.");
    } catch {
      setError("Failed to add note.");
    } finally {
      setAddingNote(false);
    }
  }
  
  async function handleStatusChange() {
    if (!requestId) return;

    setUpdatingStatus(true);
    setError("");
    setSuccess("");

    try {
      const updated = await updateMaintenanceStatus(
        Number(requestId),
        { status }
      );

      setRequest(updated);
      setStatus(updated.status);

      const updatedTimeline = await getMaintenanceTimeline(
        Number(requestId)
      );
      setTimeline(updatedTimeline);

      setSuccess("Status updated successfully.");
    } catch {
      setError("Failed to update status.");
    } finally {
      setUpdatingStatus(false);
    }
  }
  
  async function handleAssignContractor() {
    if (!requestId || !selectedContractorId) return;

    setAssigningContractor(true);
    setError("");
    setSuccess("");

    try {
      await assignContractor(Number(requestId), {
        contractorId: Number(selectedContractorId),
      });

      const updatedRequest = await getMaintenanceRequest(Number(requestId));
      setRequest(updatedRequest);

      const updatedTimeline = await getMaintenanceTimeline(Number(requestId));
      setTimeline(updatedTimeline);

      setSelectedContractorId("");
      setSuccess("Contractor assigned successfully.");
    } catch {
      setError("Failed to assign contractor.");
    } finally {
      setAssigningContractor(false);
    }
  }
  
  async function handleUnassignContractor(contractorId: number) {
    if (!requestId) return;

    setError("");
    setSuccess("");

    try {
      await unassignContractor(
        Number(requestId),
        contractorId
      );

      const updatedRequest = await getMaintenanceRequest(
        Number(requestId)
      );
      setRequest(updatedRequest);

      const updatedTimeline = await getMaintenanceTimeline(
        Number(requestId)
      );
      setTimeline(updatedTimeline);

      setSuccess("Contractor removed successfully.");
    } catch {
      setError("Failed to remove contractor.");
    }
  }
  
  if (loading) {
    return <div className="text-gray-600">Loading request...</div>;
  }

  if (error && !request) {
    return (
      <div className="space-y-4">
        <button
          type="button"
          onClick={() => navigate("/maintenance")}
          className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900"
        >
          <ArrowLeft size={16} />
          Back to Maintenance
        </button>

        <p className="text-red-600">{error}</p>
      </div>
    );
  }

  if (!request) {
    return <div className="text-gray-600">Request not found.</div>;
  }

  return (
    <div className="space-y-6">
      <button
        type="button"
        onClick={() => navigate("/maintenance")}
        className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900"
      >
        <ArrowLeft size={16} />
        Back to Maintenance
      </button>

      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          Maintenance Request #{request.id}
        </h1>
        <p className="mt-1 text-gray-500">
          Request details and maintenance activity.
        </p>
      </div>

      {error && (
        <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {success && (
        <div className="rounded-md border border-green-200 bg-green-50 p-3 text-sm text-green-700">
          {success}
        </div>
      )}

      <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <div className="space-y-6">
          <div className="grid gap-6 md:grid-cols-2">
            <div>
              <p className="text-sm text-gray-500">Unit</p>
              <p className="mt-1 font-medium">
                {request.unitNumber} (ID: {request.unitId})
              </p>
            </div>

			<div>
			  <label
			    htmlFor="status"
			    className="block text-sm font-medium text-gray-700"
			  >
			    Status
			  </label>

			  <div className="mt-2 flex gap-2">
			    <select
			      id="status"
			      value={status}
			      onChange={(event) =>
			        setStatus(event.target.value as Status)
			      }
			      className="rounded-md border border-gray-300 px-3 py-2 text-sm"
			    >
			      <option value="REPORTED">REPORTED</option>
			      <option value="TRIAGED">TRIAGED</option>
			      <option value="SCHEDULED">SCHEDULED</option>
			      <option value="RESOLVED">RESOLVED</option>
			    </select>

			    <button
			      type="button"
			      onClick={handleStatusChange}
			      disabled={updatingStatus || status === request.status}
			      className="rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-50"
			    >
			      {updatingStatus ? "Updating..." : "Update Status"}
			    </button>
			  </div>
			</div>

            <div>
              <p className="text-sm text-gray-500">Created By</p>
              <p className="mt-1 font-medium">
                {request.createdByUsername}
              </p>
            </div>

			<div>
			  <p className="text-sm text-gray-500">Assigned Contractors</p>

			  {request.contractorIds.length === 0 ? (
			    <p className="mt-1 text-sm text-gray-500">
			      No contractors assigned.
			    </p>
			  ) : (
				<div className="mt-2 space-y-2">
				  {request.contractorIds.map((contractorId) => {
				    const contractor = contractors.find(
				      (item) => item.id === contractorId
				    );

				    return (
				      <div
				        key={contractorId}
				        className="flex items-center justify-between rounded-md border border-gray-200 px-3 py-2"
				      >
				        <span className="text-sm font-medium text-gray-900">
				          {contractor?.username ?? `Contractor #${contractorId}`}
				        </span>
						{isManager && (
				        <button
				          type="button"
				          onClick={() =>
				            handleUnassignContractor(contractorId)
				          }
				          className="text-sm text-red-600 hover:text-red-800"
				        >
				          Remove
				        </button>
						)}
				      </div>
				    );
				  })}
				</div>
			  )}
			  {isManager && (
			  <div className="mt-3 flex gap-2">
			    <select
			      value={selectedContractorId}
			      onChange={(event) =>
			        setSelectedContractorId(event.target.value)
			      }
			      className="rounded-md border border-gray-300 px-3 py-2 text-sm"
			    >
			      <option value="">Select contractor</option>

			      {contractors
			        .filter(
			          (contractor) =>
			            !request.contractorIds.includes(contractor.id)
			        )
			        .map((contractor) => (
			          <option key={contractor.id} value={contractor.id}>
			            {contractor.username}
			          </option>
			        ))}
			    </select>

			    <button
			      type="button"
			      onClick={handleAssignContractor}
			      disabled={assigningContractor || !selectedContractorId}
			      className="rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-50"
			    >
			      {assigningContractor ? "Assigning..." : "Assign"}
			    </button>
			  </div>
			  )}
			</div>
          </div>

          <div>
            <label
              htmlFor="description"
              className="block text-sm font-medium text-gray-700"
            >
              Description
            </label>
            <textarea
              id="description"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              rows={5}
              className="mt-2 w-full rounded-md border border-gray-300 px-3 py-2 text-sm outline-none focus:border-gray-500"
            />
          </div>

          <div>
            <label
              htmlFor="priority"
              className="block text-sm font-medium text-gray-700"
            >
              Priority
            </label>
            <select
              id="priority"
              value={priority}
              onChange={(event) =>
                setPriority(event.target.value as Priority)
              }
              className="mt-2 rounded-md border border-gray-300 px-3 py-2 text-sm"
            >
              <option value="LOW">LOW</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="HIGH">HIGH</option>
              <option value="URGENT">URGENT</option>
            </select>
          </div>

          <button
            type="button"
            onClick={handleSave}
            disabled={saving}
            className="rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {saving ? "Saving..." : "Save Changes"}
          </button>
        </div>
      </div>

      <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <div className="grid gap-6 md:grid-cols-2">
          <div>
            <p className="text-sm text-gray-500">Created</p>
            <p className="mt-1 text-sm">
              {new Date(request.createdAt).toLocaleString()}
            </p>
          </div>

          <div>
            <p className="text-sm text-gray-500">Last Updated</p>
            <p className="mt-1 text-sm">
              {new Date(request.updatedAt).toLocaleString()}
            </p>
          </div>
        </div>
      </div>
	  <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
	    <h2 className="text-lg font-semibold text-gray-900">
	      Activity Timeline
	    </h2>
		<div className="mb-6">
		  <label
		    htmlFor="maintenance-note"
		    className="block text-sm font-medium text-gray-700"
		  >
		    Add Note
		  </label>

		  <textarea
		    id="maintenance-note"
		    value={note}
		    onChange={(event) => setNote(event.target.value)}
		    rows={3}
		    placeholder="Add a maintenance note..."
		    className="mt-2 w-full rounded-md border border-gray-300 px-3 py-2 text-sm outline-none focus:border-gray-500"
		  />

		  <button
		    type="button"
		    onClick={handleAddNote}
		    disabled={addingNote || !note.trim()}
		    className="mt-2 rounded-md bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-50"
		  >
		    {addingNote ? "Adding..." : "Add Note"}
		  </button>
		</div>
	    {timelineLoading ? (
	      <p className="mt-4 text-sm text-gray-500">
	        Loading timeline...
	      </p>
	    ) : timeline.length === 0 ? (
	      <p className="mt-4 text-sm text-gray-500">
	        No timeline events yet.
	      </p>
	    ) : (
	      <div className="mt-4 space-y-4">
	        {timeline.map((event) => (
	          <div
	            key={event.id}
	            className="border-l-2 border-gray-300 pl-4"
	          >
	            <div className="flex items-center justify-between gap-4">
	              <p className="font-medium text-gray-900">
	                {event.eventType}
	              </p>

	              <p className="text-xs text-gray-500">
	                {new Date(event.createdAt).toLocaleString()}
	              </p>
	            </div>

	            {event.oldStatus || event.newStatus ? (
	              <p className="mt-1 text-sm text-gray-600">
	                {event.oldStatus ?? "—"} → {event.newStatus ?? "—"}
	              </p>
	            ) : null}

	            {event.note ? (
	              <p className="mt-1 text-sm text-gray-700">
	                {event.note}
	              </p>
	            ) : null}

	            <p className="mt-1 text-xs text-gray-500">
	              By {event.performedByUsername}
	              {event.contractorUsername
	                ? ` · Contractor: ${event.contractorUsername}`
	                : ""}
	            </p>
	          </div>
	        ))}
	      </div>
	    )}
	  </div>
    </div>
  );
}