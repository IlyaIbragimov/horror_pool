import { useCallback, useEffect, useRef, useState } from "react";

export function useAsyncResource<T>(load: () => Promise<T>) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const requestId = useRef(0);

  const reload = useCallback(async () => {
    const currentRequestId = ++requestId.current;

    setLoading(true);
    setError(null);

    try {
      const result = await load();

      if (currentRequestId === requestId.current) {
        setData(result);
      }

      return result;
    } catch (error) {
      if (currentRequestId === requestId.current) {
        setError(error instanceof Error ? error.message : String(error));
      }

      return undefined;
    } finally {
      if (currentRequestId === requestId.current) {
        setLoading(false);
      }
    }
  }, [load]);

  useEffect(() => {
    void reload();

    return () => {
      requestId.current += 1;
    };
  }, [reload]);

  return { data, loading, error, reload };
}
