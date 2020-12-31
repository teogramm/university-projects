#include <memory>
#include "MyConvolve.h"
#include "Utils/Utils.h"
#include <sndfile.hh>

/**
 * Converts given SndfileHandle to mono
 * @param file the file to convert
 * @return vector of mono data
 */
std::unique_ptr<vector<float>> convertToMono(SndfileHandle& file){
    auto data = std::make_unique<float[]>(file.channels()*file.frames());
    auto mono = std::make_unique<vector<float>>();
    mono->reserve(file.frames());
    file.read(data.get(),file.channels()*file.frames());
    // For each sample keep only the first sample, ignore the rest
    for(unsigned i=0;i<file.frames();i++){
        mono->push_back(data[2*i]);
    }
    return mono;
}

void question1(){
    // Create input and output file handles
    auto pink = SndfileHandle("pink_noise.wav");
    auto sample = SndfileHandle("sample_audio.wav");
    auto out = SndfileHandle("pinkNoise_sampleAudio.wav",SFM_WRITE,SF_FORMAT_WAV|SF_FORMAT_PCM_32,1,16000);


    // Read pink_noise.wav
    auto monoPink = convertToMono(pink);

    // Read sample_audio.wav and turn it into a vector
    auto sampleData =std::make_unique<float[]>(sample.frames());
    sample.read(sampleData.get(),sample.frames());
    auto sampleDataVector = std::make_unique<std::vector<float>>(sampleData.get(),sampleData.get()+sample.frames());

    // Calculate convolution, normalize the results and write to file
    auto convolution = myConvolve<float,float>(*sampleDataVector,*monoPink);
    Helper::normalize(*convolution);
    out.write(convolution->data(), convolution->size());
}

unique_ptr<vector<float>> createWhiteNoiseSignal(unsigned size){
    // Seed from a real random device
    std::random_device r;
    // Create the random number generator using the real random value
    auto generator = std::default_random_engine(r());
    // Create the distribution
    auto distribution = std::normal_distribution<float>();
    // Create a lambda so getting a number is easier
    auto nextRandom = [&distribution,&generator](){ return distribution(generator);};

    auto whiteNoise = make_unique<vector<float>>();
    whiteNoise->reserve(size);
    for(unsigned i=0;i<size;i++){
        whiteNoise->push_back(nextRandom());
    }
    return whiteNoise;
}

void question2(){
    auto sample = SndfileHandle("sample_audio.wav");
    auto out = SndfileHandle("whiteNoise_sampleAudio.wav",SFM_WRITE,SF_FORMAT_WAV|SF_FORMAT_PCM_32,1,16000);
    auto autoWhite = SndfileHandle("white_noise.wav",SFM_WRITE,SF_FORMAT_WAV|SF_FORMAT_PCM_32,1,16000);

    // Read sample_audio.wav
    auto framesToRead = sample.frames()/4;
    auto sampleData =std::make_unique<float[]>(framesToRead);
    sample.read(sampleData.get(),framesToRead);
    auto sampleDataVector = std::make_unique<std::vector<float>>(sampleData.get(),sampleData.get()+framesToRead);

    // Create white noise vector and normalize
    auto wNoise = createWhiteNoiseSignal(sample.frames()/2);
    Helper::normalize(*wNoise);

    auto convolution = myConvolve<float,float>(*wNoise,*sampleDataVector);
    Helper::normalize(*convolution);
    out.write(convolution->data(),convolution->size());
}