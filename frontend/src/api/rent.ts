import apiClient from "./client";

export interface RentPayment {
  id: number;
  unitId: number;
  paymentMonth: string;
  amount: number;
  createdAt: string;
}

export interface RecordRentPaymentRequest {
  paymentMonth: string;
  amount: number;
}

export interface BulkRentPaymentItem {
  unitNumber: string;
  amount: number;
}

export interface BulkRentPaymentRequest {
  paymentMonth: string;
  payments: BulkRentPaymentItem[];
}

export interface BulkRentPaymentResult {
  unitId: number;
  unitNumber: string;
  expectedAmount: number;
  paymentAmount: number | null;
  status: "MATCHED" | "UNDERPAID" | "OVERPAID" | "UNMATCHED";
}

export interface BulkRentPaymentResponse {
  paymentMonth: string;
  results: BulkRentPaymentResult[];
}

export interface RentRollRow {
  unitNumber: string;
  address: string;
  monthlyRent: number;
  tenant: string | null;
  paymentAmount: number | null;
  paymentStatus: "MATCHED" | "UNDERPAID" | "OVERPAID" | "UNMATCHED";
}

export async function recordRentPayment(
  unitId: number,
  data: RecordRentPaymentRequest
): Promise<RentPayment> {
  const response = await apiClient.post<RentPayment>(
    `/rent/units/${unitId}/payments`,
    data
  );
  return response.data;
}

export async function getRentPaymentHistory(
  unitId: number
): Promise<RentPayment[]> {
  const response = await apiClient.get<RentPayment[]>(
    `/rent/units/${unitId}/payments`
  );
  return response.data;
}

export async function getMonthlyRentPayment(
  unitId: number,
  paymentMonth: string
): Promise<RentPayment> {
  const response = await apiClient.get<RentPayment>(
    `/rent/units/${unitId}/payments/${paymentMonth}`
  );
  return response.data;
}

export async function recordBulkRentPayments(
  data: BulkRentPaymentRequest
): Promise<BulkRentPaymentResponse> {
  const response = await apiClient.post<BulkRentPaymentResponse>(
    "/rent/bulk",
    data
  );
  return response.data;
}

export async function getRentRoll(
  paymentMonth: string
): Promise<string> {
  const response = await apiClient.get<string>(
    `/rent/rent-roll?paymentMonth=${paymentMonth}`,
    {
      responseType: "text",
    }
  );

  return response.data;
}