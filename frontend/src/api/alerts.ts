import apiClient from "./client";

export interface RentAlert {
  id: number;
  unitId: number;
  unitNumber: string;
  message: string;
  alertMonth: string;
  dismissed: boolean;
  createdAt: string;
}

export async function getAlerts(): Promise<RentAlert[]> {
  const response = await apiClient.get<RentAlert[]>("/alerts");
  return response.data;
}

export async function dismissAlert(alertId: number): Promise<void> {
  await apiClient.delete(`/alerts/${alertId}`);
}