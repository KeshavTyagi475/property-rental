import {
  LogOut,
  Home,
  Building2,
  Wrench,
  CreditCard,
  Bell,
} from "lucide-react";

import { NavLink, Outlet, useNavigate } from "react-router-dom";

import { useAuth } from "../auth/AuthContext";

const navigation = [
  { name: "Dashboard", path: "/", icon: Home, managerOnly: true },
  { name: "Units", path: "/units", icon: Building2, managerOnly: true },
  { name: "Maintenance", path: "/maintenance", icon: Wrench, managerOnly: false },
  { name: "Rent", path: "/rent", icon: CreditCard, managerOnly: true },
  { name: "Alerts", path: "/alerts", icon: Bell, managerOnly: true },
];

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const isManager = user?.roles.includes("ROLE_PROPERTY_MANAGER");

  async function handleLogout() {
    await logout();
    navigate("/login", { replace: true });
  }

  const visibleNavigation = navigation.filter(
    (item) => !item.managerOnly || isManager
  );

  return (
    <div className="min-h-screen bg-gray-100">
      <aside className="fixed inset-y-0 left-0 w-64 bg-gray-900 text-white">
        <div className="border-b border-gray-700 px-6 py-5">
          <h1 className="text-lg font-bold">Property Rental</h1>
        </div>

        <nav className="space-y-1 p-4">
          {visibleNavigation.map((item) => {
            const Icon = item.icon;

            return (
              <NavLink
                key={item.path}
                to={item.path}
                end={item.path === "/"}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium ${
                    isActive
                      ? "bg-gray-700 text-white"
                      : "text-gray-300 hover:bg-gray-800 hover:text-white"
                  }`
                }
              >
                <Icon size={18} />
                {item.name}
              </NavLink>
            );
          })}
        </nav>
      </aside>

      <div className="ml-64">
        <header className="flex h-16 items-center justify-between border-b bg-white px-6">
          <div>
            <h2 className="text-lg font-semibold text-gray-900">
              Property Rental Management
            </h2>
          </div>

          <div className="flex items-center gap-4">
            <span className="text-sm text-gray-600">
              {user?.username}
            </span>

            <button
              type="button"
              onClick={handleLogout}
              className="flex items-center gap-2 rounded-lg border border-gray-300 px-3 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
            >
              <LogOut size={16} />
              Logout
            </button>
          </div>
        </header>

        <main className="p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}