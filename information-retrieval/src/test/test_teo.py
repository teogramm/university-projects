import datetime
import pickle

from SearchEngine.backend import SpeechBackend
from SearchEngine.backend.inverted.inverted_index import InvertedIndex
from SearchEngine.backend.top.keyword_manager import KeywordManager
from SearchEngine.backend.top.group_manager import GroupManager
from SearchEngine.backend.top.suggested_stopwords import suggested_stopwords
from SearchEngine.preprocessing.create_group import *
from SearchEngine.backend.speech_file import SpeechFile
import time
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer, TfidfTransformer
from SearchEngine.preprocessing.funcs import remove_accents
from SearchEngine.preprocessing.create import *
from SearchEngine.preprocessing.stopwords import STOPWORDS

from SearchEngine.preprocessing.create import create_inverted_index, create_transformer_vectorizer

# DO NOT REMOVE GUARD BELOW - Needed to run on Windows
if __name__ == "__main__":
    create_inverted_index(index_file_name="smallindex", speech_number=100)
