import apiClient from "./client";

export interface WeeklyResolvedCount {
  weekStart: string;
  resolvedCount: number;
}

export interface DashboardSummary {
  openMaintenanceRequests: number;
  overdueRentUnits: number;
  resolvedThisWeek: number;
  totalRentCollectedThisMonth: number;
  maintenanceByStatus: Record<string, number>;
  maintenanceByContractor: Record<string, number>;
  resolvedLastEightWeeks: WeeklyResolvedCount[];
}

export async function getDashboard(): Promise<DashboardSummary> {
  const response = await apiClient.get<DashboardSummary>("/dashboard");
  return response.data;
}