type Props = {
  className?: string;
  loading?: boolean;
  page: number;
  pageNumber?: number;
  totalPages?: number;
  lastPage?: boolean;
  onPageChange: (page: number) => void;
};

export function Pager({
  className,
  loading = false,
  page,
  pageNumber,
  totalPages,
  lastPage,
  onPageChange,
}: Props) {
  const displayPage = pageNumber ?? page;

  return (
    <div className={className}>
      <button
        disabled={loading || page <= 1}
        onClick={() => onPageChange(page - 1)}
      >
        Prev
      </button>

      <span>
        Page {displayPage} / {totalPages ?? "?"}
      </span>

      <button
        disabled={loading || lastPage !== false}
        onClick={() => onPageChange(page + 1)}
      >
        Next
      </button>
    </div>
  );
}
