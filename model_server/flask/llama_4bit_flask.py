from flask_cors import *
from flask import request, Flask
import logging
from jina import Client
from docarray import Document, DocumentArray

logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app, supports_credentials=True)

qa_client = Client(host="grpc://0.0.0.0:61002")


@app.route("/infer", methods=["POST", "GET"])
def infer():
    query = request.json.get("query")
    session = request.json.get("session")
    if query is None or not query:
        result = {"status": 1, "msg": "没有解析到query, 请检查传入的参数"}
        return result
    if session is None:
        result = {"status": 1, "msg": "没有解析到session, 请检查传入的参数"}
        return result

    if session:
        input_da = DocumentArray(
            [Document(text=query, tags={"session":
                                            [{"human": s["human"], "assistant": s["assistant"]} for s in session]})])
    else:
        input_da = DocumentArray([Document(text=query, tags={"session": []})])

    out_docs = qa_client.post("/", input_da)

    results = []
    for doc in out_docs:
        if not doc.text or doc.text is None or len(doc.text.strip()) < 1:
            results.append("Sorry, I haven't learned this knowledge yet, please ask me other questions^ ^")
        else:
            results.append(doc.text)
    return {"status": 0, "result": results}


if __name__ == '__main__':
    app.run('0.0.0.0', port=9502)
