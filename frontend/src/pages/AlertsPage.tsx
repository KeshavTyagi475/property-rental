import { useEffect, useState } from "react";
import { dismissAlert, getAlerts, type RentAlert } from "../api/alerts";

export default function AlertsPage() {
  const [alerts, setAlerts] = useState<RentAlert[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [dismissingId, setDismissingId] = useState<number | null>(null);

  async function loadAlerts() {
    try {
      setError("");
      const result = await getAlerts();
      setAlerts(result.filter((alert) => !alert.dismissed));
    } catch {
      setError("Unable to load alerts.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAlerts();
  }, []);

  async function handleDismiss(alertId: number) {
    setDismissingId(alertId);
    setError("");

    try {
      await dismissAlert(alertId);
      setAlerts((current) =>
        current.filter((alert) => alert.id !== alertId)
      );
    } catch {
      setError("Unable to dismiss alert.");
    } finally {
      setDismissingId(null);
    }
  }

  if (loading) {
    return <div className="text-gray-600">Loading alerts...</div>;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Alerts</h1>
        <p className="mt-1 text-gray-500">
          Rent alerts that need your attention.
        </p>
      </div>

      {error && (
        <div className="rounded-lg bg-red-50 p-4 text-sm text-red-700">
          {error}
        </div>
      )}

      {alerts.length === 0 ? (
        <div className="rounded-xl bg-white p-6 shadow-sm">
          <p className="text-sm text-gray-500">
            No active rent alerts.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {alerts.map((alert) => (
            <div
              key={alert.id}
              className="flex items-center justify-between rounded-xl bg-white p-6 shadow-sm"
            >
              <div>
                <p className="font-medium text-gray-900">
                  Unit {alert.unitNumber}
                </p>

                <p className="mt-1 text-sm text-gray-600">
                  {alert.message}
                </p>

                <p className="mt-2 text-xs text-gray-400">
                  Month: {alert.alertMonth}
                </p>
              </div>

              <button
                type="button"
                onClick={() => handleDismiss(alert.id)}
                disabled={dismissingId === alert.id}
                className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50"
              >
                {dismissingId === alert.id ? "Dismissing..." : "Dismiss"}
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}