import unittest

class TestIndex(unittest.TestCase):
    def test_create_index(self):
        from SearchEngine.preprocessing.create import create_inverted_index
        create_inverted_index(index_file_name="smallindex", speech_number=100)
