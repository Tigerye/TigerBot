export interface ImageDetailModel {
  inputImage: string;
  outputImage: string;
  processStatus: number;
  msg: String;
  text: string;
  title: string;
  desc: string;
  width: number;
  height: number;
  user: UserModel;
  follow: boolean;
  interact: InteractModel;
  id: number;
  reqId: number;
  publishTime: string;
  createTime: string;
  role: string;
  status: number; //1已发布 0未发布
  inputParam: Array<InputParamModel>;
  steps: number;
  imageStrength: number;
  promptWeight: number;
  seed: number;
  nIter: number;
  iterImages: Array<string>;
  gridImage: string;
}

export interface iterImageModel {
  url: string;
}

interface InputParamModel {
  modifiers: Array<string>;
  text: string;
  weight: number;
}

interface UserModel {
  account: string;
  avatar: string;
  name: string;
  id: number;
}

interface InteractModel {
  bizId: string;
  bizType: number;
  commentNum: number;
  shareNum: number;
  thumbUp: boolean;
  thumbUpNum: number;
  userHasComment: boolean;
  viewNum: number;
}
