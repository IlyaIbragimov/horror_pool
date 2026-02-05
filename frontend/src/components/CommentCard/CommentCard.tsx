import type { Comment } from "../../types/movie.types";
import styles from "./CommentCard.module.css";

type Props = {
  comment: Comment;
  depth: number;
  isFormOpen: boolean;
  isEditing: boolean;
  formText: string;
  onFormTextChange: (v: string) => void;
  onReplyOpen: () => void;
  onEditOpen?: () => void;
  onDelete?: () => void;
  onFormClose: () => void;
  onFormSubmit: () => void;
  disabled?: boolean;
  canEdit?: boolean;
};

export function CommentCard({
  comment,
  depth,
  isFormOpen,
  isEditing,
  formText,
  onFormTextChange,
  onReplyOpen,
  onEditOpen,
  onDelete,
  onFormClose,
  onFormSubmit,
  disabled,
  canEdit,
}: Props) {
  return (
    <div
      className={styles.comment_card}
      style={{ marginLeft: `${Math.min(depth, 6) * 24}px` }}
    >
      <div className={styles.comment_data}>
        <div className={styles.comment_user}>{comment.userName},</div>
        <div className={styles.comment_date}>left at {comment.date}</div>
      </div>

      <div className={styles.comment_text}>{comment.commentContent}</div>

      <div className={styles.comment_options}>
        {!isFormOpen ? (
          <>
            <div className={styles.comment_option}>
              <a onClick={onReplyOpen}>Reply</a>
            </div>
            {canEdit && onEditOpen && (
              <div className={styles.comment_option}>
                <a onClick={onEditOpen}>Edit</a>
              </div>
            )}
            {canEdit && onDelete && (
              <div className={styles.comment_delete}>
                <a onClick={onDelete}>Delete</a>
              </div>
            )}
          </>
        ) : (
          <div className={styles.comment_option}>
            <a onClick={onFormClose}>Cancel</a>
          </div>
        )}
      </div>

      {isFormOpen && (
        <form
          onSubmit={(e) => {
            e.preventDefault();
            onFormSubmit();
          }}
          className={styles.reply_form}
        >
          <textarea
            className={styles.reply_input}
            value={formText}
            onChange={(e) => onFormTextChange(e.target.value)}
            placeholder={isEditing ? "Edit your comment..." : "Write a reply..."}
          />
          <button
            type="submit"
            className={styles.reply_button}
            disabled={disabled || !formText.trim()}
          >
            Send
          </button>
        </form>
      )}
    </div>
  );
}
