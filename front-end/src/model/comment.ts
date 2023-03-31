export interface commentModel {
  title: string;
  id: number;
  user: UserModel;
  userId: number;
  userName: string;
  userAvatar: string;
  bizId: string;
  bizType: number;
  content: string;
  replyCommentId: number;
  replyRootId: number;
  replyUserId: number;
  replyUserName: string;
  replyUserAvatar: string;
  createTime: string;
  thumbUpNum: number;
  thumbDownNum: number;
  subCommentList: Array<commentModel>;
  thumbUp: boolean;
  thumbDown: boolean;
}

interface UserModel {
  id: number;
  name: string;
  avatar: string;
  account: string;
  role: string;
}
