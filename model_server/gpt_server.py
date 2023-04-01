import inspect
import os

if __name__ == "__main__":
    from jina import Flow

    _input = "gpt.yml"
    f = Flow.load_config(
        _input,
        extra_search_paths=[os.path.dirname(inspect.getfile(inspect.currentframe()))]
    )
    with f:
        f.block()
