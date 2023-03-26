import json
import sys
import plotext as plt
import numpy as np

with open(sys.argv[1]) as f:
    data = json.load(f)
step = []
epoch = []
loss = []
log = data["log_history"]
for v in log:
   step.append(v["step"])
   epoch.append(v["epoch"])
   loss.append(v["loss"] if "loss" in v else v["train_loss"])
   
plt.plot(epoch, loss)
plt.title("convergence")
plt.xlabel("epoch")
plt.ylabel("loss")
xticks = np.arange(0, 3, 0.1)
plt.xticks(xticks, None)
plt.show() 
