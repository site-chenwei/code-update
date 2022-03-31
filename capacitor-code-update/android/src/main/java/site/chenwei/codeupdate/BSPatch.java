package site.chenwei.codeupdate;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;

public class BSPatch {
    private RandomAccessFile mOld;
    private OutputStream mNew;
    private InputStream mPatch;

    private InputStream bzCtrl;
    private InputStream bzData;
    private InputStream bzExtra;

    private BSPatch(RandomAccessFile old, OutputStream New, InputStream patch) {
        mOld = old;
        mNew = New;
        mPatch = patch;
    }

    private static long readLong(InputStream is) throws IOException {
        byte[] bytes = new byte[8];
        is.read(bytes);
        long ret = ((long) (bytes[7] & 0x7F) << 56) | ((long) (bytes[6] & 0xFF) << 48) | ((long) (bytes[5] & 0xFF) << 40) | ((long) (bytes[4] & 0xFF) << 32) | ((long) (bytes[3] & 0xFF) << 24) | ((long) (bytes[2] & 0xFF) << 16) | ((long) (bytes[1] & 0xFF) << 8) | ((long) (bytes[0] & 0xFF));
        return ((bytes[7] & 0x80) != 0) ? -ret : ret;
    }

    private static void checkHeader(InputStream is) throws IOException {
        /* Check header for appropriate magic 'BSDIFF40' */
        byte[] bytes = new byte[8];
        is.read(bytes);
        String str = new String(bytes);
        if (!str.equals("BSDIFF40")) {
            throw new IOException("patch file doesn't start from 'BSDIFF40'" + " but with '" + str + "'");
        }
    }

    private long readPatchFile() throws IOException {
        /* Check for appropriate magic */
        checkHeader(mPatch);

        /* Read lengths from header */
        long bzCtrlLen = readLong(mPatch);
        long bzDataLen = readLong(mPatch);
        long newSize = readLong(mPatch);
        if (bzCtrlLen < 0 || bzDataLen < 0 || newSize < 0 || bzCtrlLen > Integer.MAX_VALUE || bzDataLen > Integer.MAX_VALUE) {
            throw new IOException("bzCtrlLen=" + bzCtrlLen + ", bzDataLen=" + bzDataLen + ", newSize=" + newSize);
        }

        byte[] bytes = new byte[(int) bzCtrlLen];
        mPatch.read(bytes);
        bzCtrl = new BZip2CompressorInputStream(new ByteArrayInputStream(bytes));
        bytes = new byte[(int) bzDataLen];
        mPatch.read(bytes);
        bzData = new BZip2CompressorInputStream(new ByteArrayInputStream(bytes));
        bzExtra = new BZip2CompressorInputStream(new BufferedInputStream(mPatch));

        return newSize;
    }

    private void writeDiffData(long diffLen) throws IOException {
        byte[] bytes = new byte[1024];
        byte[] bytes2 = new byte[1024];
        /* Read diff string and add old data to diff string */
        for (long i = 0; i < diffLen; ) {
            int len = (i + bytes.length > diffLen) ? (int) (diffLen - i) : bytes.length;
            len = bzData.read(bytes, 0, len);
            mOld.readFully(bytes2, 0, len);
            for (int j = 0; j < len; j++) {
                bytes[j] += bytes2[j];
            }
            mNew.write(bytes, 0, len);
            i += len;
        }
        mNew.flush();
    }

    private void writeExtraData(long extraLen) throws IOException {
        byte[] bytes = new byte[1024];
        /* Read extra string */
        for (long i = 0; i < extraLen; ) {
            int len = (i + bytes.length > extraLen) ? (int) (extraLen - i) : bytes.length;
            len = bzExtra.read(bytes, 0, len);
            mNew.write(bytes, 0, len);
            i += len;
        }
        mNew.flush();
    }

    private void patch() throws IOException {
        long newSize = readPatchFile();

        long newPos = 0;
        while (newPos < newSize) {
            /* Read control data */
            long diffLen = readLong(bzCtrl);
            long extraLen = readLong(bzCtrl);
            long adjustLen = readLong(bzCtrl);

            /* Sanity-check */
            if (newPos + diffLen > newSize) {
                throw new IOException("newPos(" + newPos + ") + diffLen(" + diffLen + ") > newSize(" + newSize + ")");
            }
            writeDiffData(diffLen);
            newPos += diffLen;

            /* Sanity-check */
            if (newPos + extraLen > newSize) {
                throw new IOException("newPos(" + newPos + ") + extraLen(" + extraLen + ") > newSize(" + newSize + ")");
            }
            writeExtraData(extraLen);
            newPos += extraLen;
            mOld.seek(mOld.getFilePointer() + adjustLen);
        }

        bzCtrl.close();
        bzData.close();
        bzExtra.close();
    }

    private void close() throws IOException {
        mOld.close();
        mNew.close();
        mPatch.close();
    }

    public static void patch(String oldPath, String newPath, String patchPath) throws IOException {
        BSPatch This = new BSPatch(new RandomAccessFile(oldPath, "r"), new FileOutputStream(newPath), new FileInputStream(patchPath));
        This.patch();
        This.close();
    }
}
