import hashlib as hasher
import datetime as date


class Block:
    def __init__(self, index, data, previous_hash):
        self.index = index
        self.timestamp = date.datetime.now()
        self.data = self.add_data(data)
        self.previoush_hash = previous_hash
        self.hash = self.hash()

    def hash(self):
        sha = hasher.sha256
        sha.update((str(self.index) +
                    str(self.timestamp) +
                    str(self.data) +
                    str(self.previoush_hash)
                    ).encode())
        return sha.hexdigest()

    def add_data(self, data):
        """
        Adds the specified data
        :param data: An instance of the data the blockchain stores
        :return: True if data is valid
        """
        return data

    def mine(self, difficulty):
        """
        Mine the block

        :param difficulty: <int> number of consecutive 0s that must be in the hash
        :return: <str> A mined hash
        """
