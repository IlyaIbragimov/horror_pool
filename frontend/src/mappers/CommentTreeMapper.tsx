import type { Comment, CommentNode } from "../types/movie.types";

export function buildCommentsTree(comments: Comment[]): CommentNode[] {
  const map = new Map<number, CommentNode>();

  comments.forEach(c => {
    map.set(c.commentId, { ...c, replies: [] });
  });

  const roots: CommentNode[] = [];

  map.forEach(node => {
    const parentId = node.parentCommentId;
    if (parentId && map.has(parentId)) {
      map.get(parentId)!.replies.push(node);
    } else {
      roots.push(node);
    }
  });

  return roots;
}