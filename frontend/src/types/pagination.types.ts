export type PaginatedResponse = {
  pageNumber: number;
  pageSize: number;
  totalElements?: number;
  totalPages: number;
  lastPage: boolean;
};
