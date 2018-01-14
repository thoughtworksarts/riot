package io.thoughtworksarts.riot;

public class FacialRecognitionWindowsAPI implements IFacialRecognitionAPI
{
    public native void Initialise();
    public native void CaptureImage();
    public native float GetCalm();
    public native float GetFear();
    public native float GetAnger();

    static
    {
        // Urgh. Loading DLLs. Java sucks. I think I will stay with C++ and Python forever.
        // For final deployment, I recommend just adding the directory with the DLL to %PATH%
        // As described in:
        // "Section 3. Modify the PATH environment variable to include the directory where the DLL is located."
        // https://www.chilkatsoft.com/java-loadLibrary-Windows.asp
        
        try 
        {
            //System.loadLibrary("CppJNITest");
 
            // NOTE: Works but totally unportable
            //System.load("C:/Coding/CppJNITest/x64/Debug/CppJNITest.dll");
        } 
        catch (UnsatisfiedLinkError e) 
        {
          System.err.println("Native code library failed to load.\n");
          e.printStackTrace();
          System.exit(1);
        }
    }
}
