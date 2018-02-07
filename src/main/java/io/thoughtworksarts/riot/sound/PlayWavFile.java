//package io.thoughtworksarts.riot.sound;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//@Slf4j
//public class PlayWavFile {
//
//    private final static int BUFFER_SIZE = 4096;
//
//
//    public void play( ) throws IOException {
//
//        FileInputStream wavFile = new FileInputStream(".wav");
//        byte[] buffer = new byte[BUFFER_SIZE];
//
//
//        // Read the first 12 bytes of the file
//        int bytesRead = wavFile.read(buffer, 0, 12);
//        if (bytesRead != 12) log.error("Not enough wav file bytes for header");
//
//
//    }
//
//    public static WavFile openWavFile(File file) throws IOException, WavFileException
//    {
//        // Instantiate new Wavfile and store the file reference
//        WavFile wavFile = new WavFile();
//        wavFile.file = file;
//
//        // Create a new file input stream for reading file data
//        wavFile.iStream = new FileInputStream(file);
//
//        // Read the first 12 bytes of the file
//        int bytesRead = wavFile.iStream.read(wavFile.buffer, 0, 12);
//        if (bytesRead != 12) throw new WavFileException("Not enough wav file bytes for header");
//
//        // Extract parts from the header
//        long riffChunkID = getLE(wavFile.buffer, 0, 4);
//        long chunkSize = getLE(wavFile.buffer, 4, 4);
//        long riffTypeID = getLE(wavFile.buffer, 8, 4);
//
//        // Check the header bytes contains the correct signature
//        if (riffChunkID != RIFF_CHUNK_ID) throw new WavFileException("Invalid Wav Header data, incorrect riff chunk ID");
//        if (riffTypeID != RIFF_TYPE_ID) throw new WavFileException("Invalid Wav Header data, incorrect riff type ID");
//
//        // Check that the file size matches the number of bytes listed in header
//        if (file.length() != chunkSize+8) {
//            throw new WavFileException("Header chunk size (" + chunkSize + ") does not match file size (" + file.length() + ")");
//        }
//
//        boolean foundFormat = false;
//        boolean foundData = false;
//
//        // Search for the Format and Data Chunks
//        while (true)
//        {
//            // Read the first 8 bytes of the chunk (ID and chunk size)
//            bytesRead = wavFile.iStream.read(wavFile.buffer, 0, 8);
//            if (bytesRead == -1) throw new WavFileException("Reached end of file without finding format chunk");
//            if (bytesRead != 8) throw new WavFileException("Could not read chunk header");
//
//            // Extract the chunk ID and Size
//            long chunkID = getLE(wavFile.buffer, 0, 4);
//            chunkSize = getLE(wavFile.buffer, 4, 4);
//
//            // Word align the chunk size
//            // chunkSize specifies the number of bytes holding data. However,
//            // the data should be word aligned (2 bytes) so we need to calculate
//            // the actual number of bytes in the chunk
//            long numChunkBytes = (chunkSize%2 == 1) ? chunkSize+1 : chunkSize;
//
//            if (chunkID == FMT_CHUNK_ID)
//            {
//                // Flag that the format chunk has been found
//                foundFormat = true;
//
//                // Read in the header info
//                bytesRead = wavFile.iStream.read(wavFile.buffer, 0, 16);
//
//                // Check this is uncompressed data
//                int compressionCode = (int) getLE(wavFile.buffer, 0, 2);
//                if (compressionCode != 1) throw new WavFileException("Compression Code " + compressionCode + " not supported");
//
//                // Extract the format information
//                wavFile.numChannels = (int) getLE(wavFile.buffer, 2, 2);
//                wavFile.sampleRate = getLE(wavFile.buffer, 4, 4);
//                wavFile.blockAlign = (int) getLE(wavFile.buffer, 12, 2);
//                wavFile.validBits = (int) getLE(wavFile.buffer, 14, 2);
//
//                if (wavFile.numChannels == 0) throw new WavFileException("Number of channels specified in header is equal to zero");
//                if (wavFile.blockAlign == 0) throw new WavFileException("Block Align specified in header is equal to zero");
//                if (wavFile.validBits < 2) throw new WavFileException("Valid Bits specified in header is less than 2");
//                if (wavFile.validBits > 64) throw new WavFileException("Valid Bits specified in header is greater than 64, this is greater than a long can hold");
//
//                // Calculate the number of bytes required to hold 1 sample
//                wavFile.bytesPerSample = (wavFile.validBits + 7) / 8;
//                if (wavFile.bytesPerSample * wavFile.numChannels != wavFile.blockAlign)
//                    throw new WavFileException("Block Align does not agree with bytes required for validBits and number of channels");
//
//                // Account for number of format bytes and then skip over
//                // any extra format bytes
//                numChunkBytes -= 16;
//                if (numChunkBytes > 0) wavFile.iStream.skip(numChunkBytes);
//            }
//            else if (chunkID == DATA_CHUNK_ID)
//            {
//                // Check if we've found the format chunk,
//                // If not, throw an exception as we need the format information
//                // before we can read the data chunk
//                if (foundFormat == false) throw new WavFileException("Data chunk found before Format chunk");
//
//                // Check that the chunkSize (wav data length) is a multiple of the
//                // block align (bytes per frame)
//                if (chunkSize % wavFile.blockAlign != 0) throw new WavFileException("Data Chunk size is not multiple of Block Align");
//
//                // Calculate the number of frames
//                wavFile.numFrames = chunkSize / wavFile.blockAlign;
//
//                // Flag that we've found the wave data chunk
//                foundData = true;
//
//                break;
//            }
//            else
//            {
//                // If an unknown chunk ID is found, just skip over the chunk data
//                wavFile.iStream.skip(numChunkBytes);
//            }
//        }
//
//        // Throw an exception if no data chunk has been found
//        if (foundData == false) throw new WavFileException("Did not find a data chunk");
//
//        // Calculate the scaling factor for converting to a normalised double
//        if (wavFile.validBits > 8)
//        {
//            // If more than 8 validBits, data is signed
//            // Conversion required dividing by magnitude of max negative value
//            wavFile.floatOffset = 0;
//            wavFile.floatScale = 1 << (wavFile.validBits - 1);
//        }
//        else
//        {
//            // Else if 8 or less validBits, data is unsigned
//            // Conversion required dividing by max positive value
//            wavFile.floatOffset = -1;
//            wavFile.floatScale = 0.5 * ((1 << wavFile.validBits) - 1);
//        }
//
//        wavFile.bufferPointer = 0;
//        wavFile.bytesRead = 0;
//        wavFile.frameCounter = 0;
//        wavFile.ioState = IOState.READING;
//
//        return wavFile;
//    }
//
//}
