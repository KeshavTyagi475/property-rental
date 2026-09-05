import apiClient from "./client";

export type Priority = "LOW" | "MEDIUM" | "HIGH" | "URGENT";
export type Status = "REPORTED" | "TRIAGED" | "SCHEDULED" | "RESOLVED";

export interface MaintenanceRequest {
  id: number;
  unitId: number;
  unitNumber: string;
  description: string;
  priority: Priority;
  status: Status;
  createdById: number;
  createdByUsername: string;
  createdAt: string;
  updatedAt: string;
  contractorIds: number[];
}

export interface CreateMaintenanceRequest {
  unitId: number;
  description: string;
  priority: Priority;
}

export interface UpdateMaintenanceRequest {
  description: string;
  priority: Priority;
}

export async function createMaintenanceRequest(
  data: CreateMaintenanceRequest,
): Promise<MaintenanceRequest> {
  const response = await apiClient.post<MaintenanceRequest>(
    "/maintenance/requests",
    data,
  );
  return response.data;
}

export async function updateMaintenanceRequest(
  requestId: number,
  data: UpdateMaintenanceRequest,
): Promise<MaintenanceRequest> {
  const response = await apiClient.put<MaintenanceRequest>(
    `/maintenance/${requestId}`,
    data,
  );
  return response.data;
}

export interface MaintenanceSearchParams {
  text?: string;
  unitId?: number;
  status?: Status;
  contractorId?: number;
  priority?: Priority;
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: "asc" | "desc";
}

export interface MaintenancePage {
  content: MaintenanceRequest[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export async function searchMaintenanceRequests(
  params: MaintenanceSearchParams = {},
): Promise<MaintenancePage> {
  const response = await apiClient.get<MaintenancePage>(
    "/maintenance/requests/search",
    { params },
  );

  return response.data;
}

export async function getContractorRequests(): Promise<MaintenanceRequest[]> {
  const response = await apiClient.get<MaintenanceRequest[]>(
    "/maintenance/contractor/requests",
  );
  return response.data;
}

export interface UpdateMaintenanceStatusRequest {
  status: Status;
}

export async function updateMaintenanceStatus(
  requestId: number,
  data: UpdateMaintenanceStatusRequest,
): Promise<MaintenanceRequest> {
  const response = await apiClient.put<MaintenanceRequest>(
    `/maintenance/requests/${requestId}/status`,
    data,
  );

  return response.data;
}

export async function getMaintenanceRequest(
  requestId: number,
): Promise<MaintenanceRequest> {
  const response = await apiClient.get<MaintenanceRequest>(
    `/maintenance/requests/${requestId}`,
  );

  return response.data;
}
export interface MaintenanceTimeline {
  id: number;
  eventType: string;
  oldStatus: Status | null;
  newStatus: Status | null;
  note: string | null;
  contractorId: number | null;
  contractorUsername: string | null;
  performedById: number;
  performedByUsername: string;
  createdAt: string;
}

export async function getMaintenanceTimeline(
  requestId: number
): Promise<MaintenanceTimeline[]> {
  const response = await apiClient.get<MaintenanceTimeline[]>(
    `/maintenance/${requestId}/timeline`
  );
  return response.data;
}

export interface AddMaintenanceNoteRequest {
  note: string;
}

export async function addMaintenanceNote(
  requestId: number,
  data: AddMaintenanceNoteRequest
): Promise<MaintenanceTimeline> {
  const response = await apiClient.post<MaintenanceTimeline>(
    `/maintenance/requests/${requestId}/timeline/notes`,
    data
  );
  return response.data;
}

export interface Contractor {
  id: number;
  username: string;
  role: string;
  createdAt: string;
}

export async function getContractors(): Promise<Contractor[]> {
  const response = await apiClient.get<Contractor[]>("/users/contractors");
  return response.data;
}

export interface AssignContractorRequest {
  contractorId: number;
}

export async function assignContractor(
  requestId: number,
  data: AssignContractorRequest
): Promise<MaintenanceAssignment> {
  const response = await apiClient.post<MaintenanceAssignment>(
    `/maintenance/requests/${requestId}/assignments`,
    data
  );
  return response.data;
}

export interface MaintenanceAssignment {
  id: number;
  contractorId: number;
  contractorUsername: string;
  assignedAt: string;
}

export async function unassignContractor(
  requestId: number,
  contractorId: number
): Promise<void> {
  await apiClient.delete(
    `/maintenance/requests/${requestId}/assignments/${contractorId}`
  );
}