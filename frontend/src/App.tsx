import React from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { useAuth } from "./auth/AuthContext";
import LoginPage from "./pages/LoginPage";
import Layout from "./components/Layout";
import DashboardPage from "./pages/DashboardPage";
import UnitsPage from "./pages/UnitsPage";
import UnitDetailPage from "./pages/UnitDetailPage";
import MaintenancePage from "./pages/MaintenancePage";
import MaintenanceRequestDetailPage from "./pages/MaintenanceRequestDetailPage";
import RentPage from "./pages/RentPage";
import AlertsPage from "./pages/AlertsPage";

function ProtectedRoute() {
  const { user, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return <Layout />;
}

function ManagerRoute({ children }: { children: React.ReactNode }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (!user.roles.includes("ROLE_PROPERTY_MANAGER")) {
    return <Navigate to="/maintenance" replace />;
  }

  return children;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

		<Route path="/" element={<ProtectedRoute />}>
		<Route
		  index
		  element={
		    <ManagerRoute>
		      <DashboardPage />
		    </ManagerRoute>
		  }
		/>

		<Route
		  path="units"
		  element={
		    <ManagerRoute>
		      <UnitsPage />
		    </ManagerRoute>
		  }
		/>
		<Route
		  path="rent"
		  element={
		    <ManagerRoute>
		      <RentPage />
		    </ManagerRoute>
		  }
		/>
		<Route
		  path="/alerts"
		  element={
		    <ManagerRoute>
		      <AlertsPage />
		    </ManagerRoute>
		  }
		/>
		<Route
		  path="units/:unitId"
		  element={
		    <ManagerRoute>
		      <UnitDetailPage />
		    </ManagerRoute>
		  }
		/>
		  <Route path="maintenance" element={<MaintenancePage />} />
		  <Route
		    path="maintenance/:requestId"
		    element={<MaintenanceRequestDetailPage />}
		  />
		</Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;