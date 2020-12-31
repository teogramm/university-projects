import datetime
import gzip
import pickle
from collections import defaultdict

from sklearn.decomposition import TruncatedSVD
from sklearn.feature_extraction.text import TfidfVectorizer

from SearchEngine import SpeechFile
from SearchEngine.backend import SpeechBackend
from SearchEngine.backend.lsa.lsa_manager import LSAManager
from SearchEngine.preprocessing.create import create_similarity_matrix
from SearchEngine.backend.similarity.similarity_manager import SimilarityManager
from SearchEngine.backend.top.group_manager import GroupManager
from SearchEngine.preprocessing.extract_processed_speeches import extract_processed_speeches
from SearchEngine.preprocessing.create_lsa import create_sampled_lsa, sample_speeches
from SearchEngine.preprocessing.create_ai import create_model, create_sampled_model
import time

from SearchEngine.preprocessing.funcs import process_raw_speech_text


# speech_file = SpeechFile("speeches.csv"
b = SpeechBackend()
print(b.get_keywords('speech', '0', datetime.date(1980,1,1), datetime.date(2021,1,1)))