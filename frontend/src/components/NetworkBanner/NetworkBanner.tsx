import React from 'react';
import { useNetworkStatus } from '../../hooks/useNetworkStatus';
import './NetworkBanner.css';

const NetworkBanner: React.FC = () => {
  const isOnline = useNetworkStatus();

  console.log('[NetworkBanner] Рендер. isOnline =', isOnline);

  if (isOnline) return null;

  return (
    <div className="network-banner" role="alert" aria-live="assertive">
      <span>Нет подключения к интернету. Штрихи сохраняются локально и будут отправлены при восстановлении связи.</span>
    </div>
  );
};

export default NetworkBanner;