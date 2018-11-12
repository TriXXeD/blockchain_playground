import block


# For simplicity Blocks could simply be presented as a Dictionary
class Blockchain(object):
    def __init__(self):
        pass

    def register_node(self, address):
        """
        Add new node to list of nodes, i.e. new address

        :param address: e.g. http://192.0.0.1:8080
        """

    def validate_chain(self, chain):
        """

        :param chain: A Blockchain
        :return:  True if valid, False is invalid
        """

    def ensure_longest(self):
        """
        Consensus Algorithm, replaces current chain if it is not the longest chain

        :return: True if longest, False if replaced
        """

    def new_block(self, proof, previous_hash):
        """
        Adds a new block to the chain

        :param proof: <int> Proof per the implemented PoW algorithm
        :param previous_hash: <str> Hash of the previous block
        :return: A new valid block
        """

    def new_transaction(self, sender, receiver, amount):
        """
        Creates a new transaction in the transaction_pool

        :param sender: Address/Public Key of the sender
        :param receiver: Address/Public Key of the receiver
        :param amount: <int> Amount of coins to transfer
        :return: <int> Index of the block that will hold transaction
        """

    def proof_of_work(self, last_block):
        """
        PoW algorithm for mining

        :param last_block: The latest Block of the valid chain
        :return: <int>
        """

    @staticmethod
    def validate_proof(last_proof, proof, last_hash):
        """
        Validates the proof of work

        :param last_proof: <int> Previous proof of work
        :param proof: <int> Current Proof
        :param last_hash: <str> Hash of the previous Block
        :return: <bool> True if valid
        """
