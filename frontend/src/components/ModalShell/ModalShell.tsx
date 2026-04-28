import type { ReactNode } from "react";

type Props = {
  title: ReactNode;
  onClose: () => void;
  children: ReactNode;
  overlayClassName: string;
  modalClassName: string;
  headerClassName: string;
  titleClassName: string;
  closeButtonClassName: string;
  beforeHeader?: ReactNode;
};

export function ModalShell({
  title,
  onClose,
  children,
  overlayClassName,
  modalClassName,
  headerClassName,
  titleClassName,
  closeButtonClassName,
  beforeHeader,
}: Props) {
  const onOverlayMouseDown = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) onClose();
  };

  const onKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === "Escape") onClose();
  };

  return (
    <div
      className={overlayClassName}
      onMouseDown={onOverlayMouseDown}
      onKeyDown={onKeyDown}
      aria-modal="true"
      role="dialog"
    >
      <div className={modalClassName}>
        {beforeHeader}
        <div className={headerClassName}>
          <h1 className={titleClassName}>{title}</h1>
          <button
            className={closeButtonClassName}
            onClick={onClose}
            type="button"
            aria-label="Close"
          >
            X
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}
