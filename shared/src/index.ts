export type TenantId = string;
export type UserId = string;

export interface TenantContext {
  tenantId: TenantId;
  userId?: UserId;
  roles: string[];
  locale?: string;
  timezone?: string;
}

export interface AuditableEntity {
  createdAt: string;
  updatedAt: string;
}

export interface Pagination {
  page: number;
  pageSize: number;
}

export interface PaginatedResult<T> extends Pagination {
  total: number;
  items: T[];
}

export interface ApiErrorDetail {
  code: string;
  message: string;
  field?: string;
  meta?: Record<string, unknown>;
}

export interface ApiResponse<T> {
  data: T;
  errors?: ApiErrorDetail[];
  meta?: Record<string, unknown>;
}

export const DEFAULT_PAGE_SIZE = 20;
export const MAX_PAGE_SIZE = 100;

export function sanitizePageSize(size?: number): number {
  if (!size || Number.isNaN(size)) {
    return DEFAULT_PAGE_SIZE;
  }

  if (size < 1) {
    return 1;
  }

  if (size > MAX_PAGE_SIZE) {
    return MAX_PAGE_SIZE;
  }

  return Math.floor(size);
}

export function createPaginatedResult<T>(
  items: T[],
  total: number,
  pagination: Partial<Pagination>
): PaginatedResult<T> {
  const { page = 1, pageSize = DEFAULT_PAGE_SIZE } = pagination;

  return {
    items,
    total,
    page,
    pageSize: sanitizePageSize(pageSize)
  };
}

export function buildTenantContext(
  tenantId: TenantId,
  options: Partial<Omit<TenantContext, "tenantId">> = {}
): TenantContext {
  const roles = options.roles ?? [];

  return {
    tenantId,
    roles,
    userId: options.userId,
    locale: options.locale,
    timezone: options.timezone
  };
}
