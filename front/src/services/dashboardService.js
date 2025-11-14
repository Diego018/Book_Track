import { apiClient } from './apiClient'

export function fetchDashboardSummary() {
  return apiClient('/api/dashboard/summary')
}
