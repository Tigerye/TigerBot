import { number } from '@intlify/core-base';

export interface UnReadCountModel {
  count: number;
  notifyType: number; //1订阅消息 2系统消息  3回复我的 4收到的赞
  notifyTypeName: string;
}

export interface SystemMsgModel {
  bizId: string;
  messageEntity: string;
  title: string;
  createTime: string;
}

// export interface CommentModel{
//   businessDetail
// }

// interface BusinessDetailModel{

// }
