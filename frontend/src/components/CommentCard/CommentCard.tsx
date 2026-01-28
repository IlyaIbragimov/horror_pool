import type { Comment } from "../../types/movie.types";
import styles from "./CommentCard.module.css";

type Props = {
  comment: Comment;
  isReplyOpen: boolean;
  replyText: string;
  onReplyTextChange: (v: string) => void;
  onReplyOpen: () => void;
  onReplyClose: () => void;
  onReplySubmit: () => void;
  disabled?: boolean;
};

export function CommentCard({
  comment,
  isReplyOpen,
  replyText,
  onReplyTextChange,
  onReplyOpen,
  onReplyClose,
  onReplySubmit,
  disabled,
}: Props) {
  return (
    <div className={styles.comment_card}>
      <div className={styles.comment_data}>
        <div className={styles.comment_user}>{comment.userName},</div>
        <div className={styles.comment_date}>left at {comment.date}</div>
      </div>

      <div className={styles.comment_text}>{comment.commentContent}</div>

      <div className={styles.comment_options}>
        {!isReplyOpen ? (
          <div className={styles.comment_response}>
            <a onClick={onReplyOpen}>Reply</a>
          </div>
        ) : (
          <div className={styles.comment_response}>
            <a onClick={onReplyClose}>Cancel</a>
          </div>
        )}
      </div>

      {isReplyOpen && (
        <form
          onSubmit={(e) => {
            e.preventDefault();
            onReplySubmit();
          }}
          className={styles.reply_form}
        >
          <textarea
            value={replyText}
            onChange={(e) => onReplyTextChange(e.target.value)}
            placeholder="Write a reply..."
          />
          <button type="submit" disabled={disabled || !replyText.trim()}>
            Send
          </button>
        </form>
      )}
    </div>
  );
}
