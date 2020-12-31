import unittest

class TestImports(unittest.TestCase):
    def test_main_package(self):
        import SearchEngine

    def test_main_api(self):
        from SearchEngine import Speech, SearchEngine, SpeechFile, index
