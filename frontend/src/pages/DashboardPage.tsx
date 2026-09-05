import { useEffect, useState } from "react";
import {
  BarChart3,
  Building2,
  CircleDollarSign,
  Wrench,
} from "lucide-react";
import {
  getDashboard,
  type DashboardSummary,
} from "../api/dashboard";

const cards = [
  {
    key: "openMaintenanceRequests",
    title: "Open Maintenance",
    icon: Wrench,
  },
  {
    key: "overdueRentUnits",
    title: "Rent Overdue",
    icon: CircleDollarSign,
  },
  {
    key: "resolvedThisWeek",
    title: "Resolved This Week",
    icon: BarChart3,
  },
  {
    key: "totalRentCollectedThisMonth",
    title: "Rent Collected",
    icon: Building2,
  },
] as const;

export default function DashboardPage() {
  const [dashboard, setDashboard] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getDashboard()
      .then((data) => {
        setDashboard(data);
      })
      .catch(() => {
        setError("Unable to load dashboard data.");
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-2 text-sm text-gray-500">
          Loading dashboard data...
        </p>
      </div>
    );
  }

  if (error || !dashboard) {
    return (
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-2 text-sm text-red-600">
          {error || "Unable to load dashboard data."}
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-1 text-sm text-gray-500">
          Overview of your property rental operations.
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {cards.map((card) => {
          const Icon = card.icon;

          const value =
            card.key === "totalRentCollectedThisMonth"
              ? `₹${dashboard[card.key].toLocaleString()}`
              : dashboard[card.key];

          return (
            <div
              key={card.key}
              className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-500">{card.title}</p>
                  <p className="mt-2 text-2xl font-bold text-gray-900">
                    {value}
                  </p>
                </div>

                <div className="rounded-lg bg-gray-100 p-3">
                  <Icon size={22} className="text-gray-700" />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <section className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
          <h2 className="text-lg font-semibold text-gray-900">
            Maintenance by Status
          </h2>

          <div className="mt-4 space-y-3">
            {Object.entries(dashboard.maintenanceByStatus).map(
              ([status, count]) => (
                <div
                  key={status}
                  className="flex items-center justify-between"
                >
                  <span className="text-sm text-gray-600">{status}</span>
                  <span className="font-semibold text-gray-900">{count}</span>
                </div>
              ),
            )}
          </div>
        </section>

        <section className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
          <h2 className="text-lg font-semibold text-gray-900">
            Maintenance by Contractor
          </h2>

          <div className="mt-4 space-y-3">
            {Object.entries(dashboard.maintenanceByContractor).map(
              ([contractor, count]) => (
                <div
                  key={contractor}
                  className="flex items-center justify-between"
                >
                  <span className="text-sm text-gray-600">{contractor}</span>
                  <span className="font-semibold text-gray-900">{count}</span>
                </div>
              ),
            )}
          </div>
        </section>
      </div>

      <section className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
        <h2 className="text-lg font-semibold text-gray-900">
          Resolved Requests — Last 8 Weeks
        </h2>

        <div className="mt-4 space-y-3">
          {dashboard.resolvedLastEightWeeks.map((week) => (
            <div
              key={week.weekStart}
              className="flex items-center justify-between"
            >
              <span className="text-sm text-gray-600">
                {week.weekStart}
              </span>
              <span className="font-semibold text-gray-900">
                {week.resolvedCount}
              </span>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}