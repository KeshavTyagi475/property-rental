import apiClient from "./client";

export interface Unit {
  id: number;
  unitNumber: string;
  address: string;
  monthlyRent: number;
  currentTenant: string | null;
  archived: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUnitRequest {
  unitNumber: string;
  address: string;
  monthlyRent: number;
  currentTenant?: string | null;
}

export interface UpdateUnitRequest {
  unitNumber: string;
  address: string;
  monthlyRent: number;
  currentTenant?: string | null;
}

export async function getUnits(): Promise<Unit[]> {
  const response = await apiClient.get<Unit[]>("/units");
  return response.data;
}

export async function getUnit(id: number): Promise<Unit> {
  const response = await apiClient.get<Unit>(`/units/${id}`);
  return response.data;
}

export async function createUnit(
  data: CreateUnitRequest,
): Promise<Unit> {
  const response = await apiClient.post<Unit>("/units", data);
  return response.data;
}

export async function updateUnit(
  id: number,
  data: UpdateUnitRequest,
): Promise<Unit> {
  const response = await apiClient.put<Unit>(`/units/${id}`, data);
  return response.data;
}

export async function archiveUnit(id: number): Promise<Unit> {
  const response = await apiClient.post<Unit>(`/units/${id}/archive`);
  return response.data;
}

export async function restoreUnit(id: number): Promise<Unit> {
  const response = await apiClient.post<Unit>(`/units/${id}/restore`);
  return response.data;
}

export interface UnitMaintenanceRequest {
  id: number;
  unitId: number;
  unitNumber: string;
  description: string;
  priority: string;
  status: string;
  createdById: number;
  createdByUsername: string;
  createdAt: string;
  updatedAt: string;
  contractorIds: number[];
}

export async function getUnitMaintenance(
  id: number,
): Promise<UnitMaintenanceRequest[]> {
  const response = await apiClient.get<UnitMaintenanceRequest[]>(
    `/units/${id}/maintenance`,
  );
  return response.data;
}