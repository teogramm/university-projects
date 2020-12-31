from SearchEngine import SpeechFile
from SearchEngine.preprocessing.create import create_inverted_index, create_transformer_vectorizer
from SearchEngine.preprocessing.create_group import *

create_inverted_index(index_file_name="smallindex", speech_number=1000)
speech_file = SpeechFile("speeches.csv")
create_transformer_vectorizer(speech_file)
create_groups(speech_file, [speaker_name, party])
