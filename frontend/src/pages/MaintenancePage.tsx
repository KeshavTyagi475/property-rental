import { useEffect, useState } from "react";
import {
  createMaintenanceRequest,
  getContractorRequests,
  searchMaintenanceRequests,
  updateMaintenanceStatus,
  type MaintenanceRequest,
  type Priority,
  type Status,
} from "../api/maintenance";
import { useAuth } from "../auth/AuthContext";
import { getUnits, type Unit } from "../api/units";
import { useNavigate } from "react-router-dom";

export default function MaintenancePage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [searchText, setSearchText] = useState("");
  const [searchLoading, setSearchLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState<Status | "">("");
  const [requests, setRequests] = useState<MaintenanceRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [priorityFilter, setPriorityFilter] = useState<Priority | "">("");
  const isContractor = user?.roles.includes("ROLE_MAINTENANCE_CONTRACTOR");
  const [units, setUnits] = useState<Unit[]>([]);
  const [unitFilter, setUnitFilter] = useState("");
  const [sortBy, setSortBy] = useState("createdAt");
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("desc");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [createUnitId, setCreateUnitId] = useState("");
  const [createDescription, setCreateDescription] = useState("");
  const [createPriority, setCreatePriority] = useState<Priority>("MEDIUM");
  const [creating, setCreating] = useState(false);
  
  async function handleCreate() {
    if (!createUnitId || !createDescription.trim()) {
      setError("Unit and description are required.");
      return;
    }

    setCreating(true);
    setError("");

    try {
      await createMaintenanceRequest({
        unitId: Number(createUnitId),
        description: createDescription.trim(),
        priority: createPriority,
      });

      setCreateDescription("");
      setCreateUnitId("");
      setCreatePriority("MEDIUM");
      setShowCreateForm(false);

      await handleSearch(0);
    } catch {
      setError("Unable to create maintenance request.");
    } finally {
      setCreating(false);
    }
  }
  
  async function handleSearch(page = 0) {
    setSearchLoading(true);
    setError("");

    try {
      const result = await searchMaintenanceRequests({
        text: searchText || undefined,
        status: statusFilter || undefined,
		priority: priorityFilter || undefined,
		unitId: unitFilter ? Number(unitFilter) : undefined,
        page,
        size: 20,
		sortBy,
		direction: sortDirection,
      });

      setRequests(result.content);
	  setCurrentPage(result.number);
	  setTotalPages(result.totalPages);
	  setTotalElements(result.totalElements);
    } catch {
      setError("Unable to search maintenance requests.");
    } finally {
      setSearchLoading(false);
    }
  }
  
  async function handleStatusChange(
    requestId: number,
    status: Status,
  ) {
    setError("");

    try {
      const updatedRequest = await updateMaintenanceStatus(
        requestId,
        { status },
      );

      setRequests((currentRequests) =>
        currentRequests.map((request) =>
          request.id === requestId ? updatedRequest : request,
        ),
      );
    } catch (error) {
      setError(
        error instanceof Error
          ? error.message
          : "Unable to update maintenance status.",
      );
    }
  }
  
  useEffect(() => {
    if (isContractor) return;

    getUnits()
      .then(setUnits)
      .catch(() => setError("Unable to load units."));
  }, [isContractor]);
  
  useEffect(() => {
    if (!isContractor) {
      setLoading(false);
      return;
    }

    getContractorRequests()
      .then(setRequests)
      .catch(() => setError("Unable to load maintenance requests."))
      .finally(() => setLoading(false));
  }, [isContractor]);

  if (loading) {
    return <div className="text-gray-600">Loading maintenance...</div>;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          Maintenance
        </h1>
        <p className="mt-1 text-gray-500">
          Manage and track property maintenance requests.
        </p>
      </div>

      {error && (
        <div className="rounded-lg bg-red-50 p-4 text-sm text-red-700">
          {error}
        </div>
      )}

	  {!isContractor && (
		<>
		<div className="rounded-xl bg-white p-6 shadow-sm">
		  <div className="flex items-center justify-between">
		    <div>
		      <h2 className="text-lg font-semibold text-gray-900">
		        Maintenance Requests
		      </h2>
		      <p className="mt-1 text-sm text-gray-500">
		        Create a maintenance request for a unit.
		      </p>
		    </div>

		    <button
		      type="button"
		      onClick={() => setShowCreateForm((value) => !value)}
		      className="rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800"
		    >
		      {showCreateForm ? "Cancel" : "Create Request"}
		    </button>
		  </div>

		  {showCreateForm && (
		    <div className="mt-6 grid gap-4 md:grid-cols-3">
		      <select
		        value={createUnitId}
		        onChange={(event) => setCreateUnitId(event.target.value)}
		        className="rounded-lg border border-gray-300 px-4 py-2 text-sm"
		      >
		        <option value="">Select Unit</option>

		        {units.map((unit) => (
		          <option key={unit.id} value={unit.id}>
		            Unit {unit.unitNumber}
		          </option>
		        ))}
		      </select>

		      <select
		        value={createPriority}
		        onChange={(event) =>
		          setCreatePriority(event.target.value as Priority)
		        }
		        className="rounded-lg border border-gray-300 px-4 py-2 text-sm"
		      >
		        <option value="LOW">Low</option>
		        <option value="MEDIUM">Medium</option>
		        <option value="HIGH">High</option>
		        <option value="URGENT">Urgent</option>
		      </select>

		      <button
		        type="button"
		        onClick={handleCreate}
		        disabled={creating}
		        className="rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:opacity-50"
		      >
		        {creating ? "Creating..." : "Create Request"}
		      </button>

		      <textarea
		        value={createDescription}
		        onChange={(event) => setCreateDescription(event.target.value)}
		        placeholder="Describe the maintenance issue..."
		        rows={4}
		        className="md:col-span-3 rounded-lg border border-gray-300 px-4 py-3 text-sm outline-none focus:border-gray-500"
		      />
		    </div>
		  )}
		</div>
	    <div className="rounded-xl bg-white p-6 shadow-sm">
	      <h2 className="text-lg font-semibold text-gray-900">
	        Search Maintenance Requests
	      </h2>

	      <div className="mt-4 flex flex-wrap gap-3">
	        <input
	          type="text"
	          value={searchText}
	          onChange={(event) => setSearchText(event.target.value)}
	          placeholder="Search descriptions..."
	          className="flex-1 rounded-lg border border-gray-300 px-4 py-2 text-sm outline-none focus:border-gray-500"
	        />
			
			<select
			  value={statusFilter}
			  onChange={(event) =>
			    setStatusFilter(event.target.value as Status | "")
			  }
			  className="rounded-lg border border-gray-300 px-4 py-2 text-sm"
			>
			  <option value="">All Statuses</option>
			  <option value="REPORTED">Reported</option>
			  <option value="TRIAGED">Triaged</option>
			  <option value="SCHEDULED">Scheduled</option>
			  <option value="RESOLVED">Resolved</option>
			</select>
			<select
			  value={priorityFilter}
			  onChange={(event) =>
			    setPriorityFilter(event.target.value as Priority | "")
			  }
			  className="rounded-lg border border-gray-300 px-4 py-2 text-sm"
			>
			  <option value="">All Priorities</option>
			  <option value="LOW">Low</option>
			  <option value="MEDIUM">Medium</option>
			  <option value="HIGH">High</option>
			  <option value="URGENT">Urgent</option>
			</select>
			<select value={unitFilter}
					    onChange={(event) => setUnitFilter(event.target.value)}
					    className="rounded-lg border border-gray-300 px-4 py-2 text-sm"
					  >
					    <option value="">All Units</option>

					    {units.map((unit) => (
					      <option key={unit.id} value={unit.id}>
					        Unit {unit.unitNumber}
					      </option>
					    ))}
			</select>
			<select
			  value={sortBy}
			  onChange={(event) => setSortBy(event.target.value)}
			  className="rounded-lg border border-gray-300 px-4 py-2 text-sm"
			>
			  <option value="createdAt">Created Date</option>
			  <option value="priority">Priority</option>
			  <option value="status">Status</option>
			</select>

			<select
			  value={sortDirection}
			  onChange={(event) =>
			    setSortDirection(event.target.value as "asc" | "desc")
			  }
			  className="rounded-lg border border-gray-300 px-4 py-2 text-sm"
			>
			  <option value="desc">Descending</option>
			  <option value="asc">Ascending</option>
			</select>
	        <button
	          type="button"
	          onClick={() => handleSearch(0)}
	          disabled={searchLoading}
	          className="rounded-lg bg-gray-900 px-5 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:opacity-50"
	        >
	          {searchLoading ? "Searching..." : "Search"}
	        </button>
	      </div>
		  
		  {!isContractor && requests.length > 0 && (
		    <div className="overflow-hidden rounded-xl bg-white shadow-sm">
			<div className="border-b border-gray-200 px-6 py-4">
			  <p className="text-sm text-gray-500">
			    {totalElements} maintenance request
			    {totalElements === 1 ? "" : "s"}
			  </p>
			</div>
			
		      <table className="min-w-full divide-y divide-gray-200">
		        <thead className="bg-gray-50">
		          <tr>
		            <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
		              Request
		            </th>
		            <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
		              Unit
		            </th>
		            <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
		              Priority
		            </th>
		            <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
		              Status
		            </th>
					<th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
					  Update Status
					</th>
		            <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
		              Description
		            </th>
		          </tr>
		        </thead>

		        <tbody className="divide-y divide-gray-200">
		          {requests.map((request) => (
		            <tr key={request.id}>
					<td className="px-6 py-4 text-sm font-medium">
					  <button
					    type="button"
					    onClick={() => navigate(`/maintenance/${request.id}`)}
					    className="text-blue-600 hover:underline"
					  >
					    #{request.id}
					  </button>
					</td>

		              <td className="px-6 py-4 text-sm text-gray-700">
		                {request.unitNumber}
		              </td>

		              <td className="px-6 py-4 text-sm text-gray-700">
		                {request.priority}
		              </td>

		              <td className="px-6 py-4 text-sm text-gray-700">
		                {request.status}
		              </td>
					  <td className="px-6 py-4">
					    <select
					      value={request.status}
					      onChange={(event) =>
					        handleStatusChange(
					          request.id,
					          event.target.value as Status,
					        )
					      }
					      className="rounded-lg border border-gray-300 px-3 py-2 text-sm"
					    >
					      <option value="REPORTED">Reported</option>
					      <option value="TRIAGED">Triaged</option>
					      <option value="SCHEDULED">Scheduled</option>
					      <option value="RESOLVED">Resolved</option>
					    </select>
					  </td>
		              <td className="px-6 py-4 text-sm text-gray-700">
		                {request.description}
		              </td>
		            </tr>
		          ))}
		        </tbody>
		      </table>
			  <div className="flex items-center justify-between border-t border-gray-200 px-6 py-4">
			    <p className="text-sm text-gray-500">
			      Page {currentPage + 1} of {Math.max(totalPages, 1)}
			    </p>

			    <div className="flex gap-2">
			      <button
			        type="button"
			        onClick={() => handleSearch(currentPage - 1)}
			        disabled={currentPage === 0 || searchLoading}
			        className="rounded-lg border border-gray-300 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
			      >
			        Previous
			      </button>

			      <button
			        type="button"
			        onClick={() => handleSearch(currentPage + 1)}
			        disabled={
			          totalPages === 0 ||
			          currentPage >= totalPages - 1 ||
			          searchLoading
			        }
			        className="rounded-lg border border-gray-300 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
			      >
			        Next
			      </button>
			    </div>
			  </div>
		    </div>
		  )}
		  
	    </div>
		</>  
	)}

      {isContractor && requests.length === 0 && !error && (
        <div className="rounded-xl bg-white p-6 shadow-sm">
          <p className="text-gray-500">
            No maintenance requests are assigned to you.
          </p>
        </div>
      )}

      {isContractor && requests.length > 0 && (
        <div className="overflow-hidden rounded-xl bg-white shadow-sm">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                  Request
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                  Unit
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                  Priority
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">
                  Description
                </th>
              </tr>
            </thead>

            <tbody className="divide-y divide-gray-200">
              {requests.map((request) => (
                <tr key={request.id}>
				<td className="px-6 py-4 text-sm font-medium">
				  <button
				    type="button"
				    onClick={() => navigate(`/maintenance/${request.id}`)}
				    className="text-blue-600 hover:underline"
				  >
				    #{request.id}
				  </button>
				</td>

                  <td className="px-6 py-4 text-sm text-gray-700">
                    {request.unitNumber}
                  </td>

                  <td className="px-6 py-4 text-sm text-gray-700">
                    {request.priority}
                  </td>

                  <td className="px-6 py-4 text-sm text-gray-700">
                    {request.status}
                  </td>

                  <td className="px-6 py-4 text-sm text-gray-700">
                    {request.description}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}