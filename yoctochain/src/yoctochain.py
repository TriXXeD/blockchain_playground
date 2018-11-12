import hashlib as hasher
import datetime as date


class Block:
    def __init__(self, index, timestamp, data, prv_hash):
        self.index = index
        self.timestamp = timestamp
        self.data = data
        self.prv_hash = prv_hash
        self.hash = self.hash()

    def hash(self):
        sha = hasher.sha256()
        sha.update((str(self.index) +
                   str(self.timestamp) +
                   str(self.data) +
                   str(self.prv_hash))
                   .encode())
        return sha.hexdigest()


def genesis():
    return Block(0, date.datetime.now(), "Genesis", "0")


def new_block(latest_block):
    this_index = latest_block.index + 1
    this_timestamp = date.datetime.now()
    this_data = "Block #" + str(this_index)
    this_prv_hash = latest_block.hash
    return Block(this_index, this_timestamp, this_data, this_prv_hash)


yoctochain = [genesis()]
prv_block = yoctochain[0]
for i in range(0, 24):
    block = new_block(prv_block)
    yoctochain.append(block)
    prv_block = block

    print("Block #{} has been added".format(block.index))
    print("Hash: #{} \n".format(block.hash))
