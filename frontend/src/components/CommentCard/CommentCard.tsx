import type { Comment } from "../../types/movie.types";
import styles from "./CommentCard.module.css";

type Props = { comment: Comment };

export function CommentCard({ comment }: Props) {

  return (
    <div className={styles.comment_card}>
        
        <div className={styles.comment_data}>
            <div className={styles.comment_user}>{comment.userName},</div>
            <div className={styles.comment_date}>left at {comment.date}</div>
        </div>
        <div className={styles.comment_text}>{comment.commentContent}</div>
        <div className={styles.comment_options}>
          <div className={styles.comment_response}>
            <a>Reply</a>
          </div>
        </div>
    </div>
  );
}