import { useState, useEffect, useCallback } from 'react';

export function useNetworkStatus() {
  const [isOnline, setIsOnline] = useState<boolean>(navigator.onLine);

  const handleStatusChange = useCallback(() => {
    setIsOnline(navigator.onLine);
    console.log('[Network] Статус изменился:', navigator.onLine ? 'ONLINE' : 'OFFLINE');
  }, []);

  useEffect(() => {
    setIsOnline(navigator.onLine);
    console.log('[Network] Инициализация. Текущий статус:', navigator.onLine ? 'ONLINE' : 'OFFLINE');

    window.addEventListener('online', handleStatusChange);
    window.addEventListener('offline', handleStatusChange);
    window.addEventListener('focus', handleStatusChange);

    return () => {
      window.removeEventListener('online', handleStatusChange);
      window.removeEventListener('offline', handleStatusChange);
      window.removeEventListener('focus', handleStatusChange);
    };
  }, [handleStatusChange]);

  return isOnline;
}