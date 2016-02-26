# MyPhotoEncryptDemo
  在android开发过程中有些时候一些重要的图片，我们不希望用户通过文件管理直接能查看，我们该怎么办呢，当然你可以把图片放在android的内部存储中，data/data/下，但毕竟android root用户一大堆，还是解决不了问题。那么我们就需要对图片进行加密，当然加密的方法有很多种，下面给大家推荐我常用的2中方法，基本上可以解决大部分问题。

    1、国际标准的AES加密，通过加密流存储文件，查看的时候需要解密，这种方法适合用于非要重要的图片加密。

    2、对图片进行处理，在存储文件的时候混入字节，让它查看不了，当我们要查看的时候，我们在读文件的时候在去掉混入的字节就可以了，这种方法适合不是很重要的图片，但是又不希望用户直接在文件管理里能看到。

    接下来就是上代码，代码才是最直接的解释：

[html] view plain copy

    package com.test.photoencrypt;  
      
    import android.graphics.Bitmap;  
    import android.graphics.BitmapFactory;  
    import android.os.Bundle;  
    import android.os.Environment;  
    import android.support.v4.app.FragmentActivity;  
    import android.view.View;  
    import android.widget.Button;  
    import android.widget.ImageView;  
    import android.widget.Toast;  
      
    import com.test.photoencrypt.utils.FileUtils;  
      
    import java.io.ByteArrayOutputStream;  
    import java.io.File;  
    import java.io.FileInputStream;  
    import java.io.FileOutputStream;  
    import java.io.IOException;  
    import java.io.InputStream;  
      
    import javax.crypto.Cipher;  
    import javax.crypto.CipherInputStream;  
    import javax.crypto.CipherOutputStream;  
    import javax.crypto.spec.SecretKeySpec;  
      
    /**  
     * Created by fuweiwei on 2015/11/25.  
     */  
    public class MainActivity extends FragmentActivity {  
        private Button encryptButton,decryptButton,byteButton,rebyteButton;  
        private ImageView img;  
        private String filePath = Environment.getExternalStorageDirectory().getPath()+ "/test/test.jpg";  
        // AES加密后的文件  
        private static final String outPath = Environment.getExternalStorageDirectory().getPath()+ "/test/encrypt.jpg";  
        // 混入字节加密后文件  
        private static final String bytePath = Environment.getExternalStorageDirectory().getPath()+ "/test/byte.jpg";  
        //AES加密使用的秘钥，注意的是秘钥的长度必须是16位  
        private static final String AES_KEY = "MyDifficultPassw";  
        //混入的字节  
        private static final String BYTE_KEY = "MyByte";  
        @Override  
        protected void onCreate(Bundle savedInstanceState) {  
            super.onCreate(savedInstanceState);  
            setContentView(R.layout.activity_main);  
            encryptButton = (Button) findViewById(R.id.main_encrypt);  
            decryptButton = (Button) findViewById(R.id.main_decrypt);  
            byteButton = (Button) findViewById(R.id.main_addbyte);  
            rebyteButton = (Button) findViewById(R.id.main_removebyte);  
            img = (ImageView) findViewById(R.id.main_img);  
            File file = new File(filePath) ;  
            try {  
                InputStream inputStream = getAssets().open("test.jpg");  
                FileUtils.writeFile(file, inputStream);  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
      
            encryptButton.setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                    try {  
                        img.setImageBitmap(null);  
                        aesEncrypt();  
                        Toast.makeText(getApplicationContext(), "加密完成",  
                                Toast.LENGTH_SHORT).show();  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
            });  
      
            decryptButton.setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                    try {  
                        aesDecrypt();  
                        Toast.makeText(getApplicationContext(), "解密完成",  
                                Toast.LENGTH_SHORT).show();  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
            });  
            byteButton.setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View view) {  
                    img.setImageBitmap(null);  
                    addByte();  
                    Toast.makeText(getApplicationContext(), "加密完成",  
                            Toast.LENGTH_SHORT).show();  
                }  
            });  
            rebyteButton.setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View view) {  
                    removeByte();  
                    Toast.makeText(getApplicationContext(), "解密完成",  
                            Toast.LENGTH_SHORT).show();  
                }  
            });  
        }  
      
        /**  
         * 混入字节加密  
         */  
        public  void addByte(){  
            try {  
                //获取图片的字节流  
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);  
                ByteArrayOutputStream baos = new ByteArrayOutputStream();  
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
                byte[] bytes = baos.toByteArray();  
                FileOutputStream fops = new FileOutputStream(bytePath);  
                //混入的字节流  
                byte[] bytesAdd = BYTE_KEY.getBytes();  
                fops.write(bytesAdd);  
                fops.write(bytes);  
                fops.flush();  
                fops.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
      
        /**  
         * 移除混入的字节解密图片  
         */  
        public  void removeByte(){  
            try {  
                FileInputStream stream = null;  
                stream = new FileInputStream(new File(bytePath));  
                ByteArrayOutputStream out = new ByteArrayOutputStream(1024);  
                byte[] b = new byte[1024];  
                int n;  
                int i=0;  
                while ((n = stream.read(b)) != -1) {  
                    if(i==0){  
                        //第一次写文件流的时候，移除我们之前混入的字节  
                        out.write(b, BYTE_KEY.length(), n-BYTE_KEY.length());  
                    }else{  
                        out.write(b, 0, n);  
                    }  
                    i++;  
                }  
                stream.close();  
                out.close();  
                //获取字节流显示图片  
                byte[] bytes= out.toByteArray();  
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);  
                img.setImageBitmap(bitmap);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
      
        /**  
         * 使用AES加密标准进行加密  
         */  
        public void aesEncrypt()  {  
            try {  
                FileInputStream fis = null;  
                fis = new FileInputStream(filePath);  
                FileOutputStream fos = new FileOutputStream(outPath);  
                //SecretKeySpec此类来根据一个字节数组构造一个 SecretKey  
                SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),  
                        "AES");  
                //Cipher类为加密和解密提供密码功能,获取实例  
                Cipher cipher = Cipher.getInstance("AES");  
                //初始化  
                cipher.init(Cipher.ENCRYPT_MODE, sks);  
                //CipherOutputStream 为加密输出流  
                CipherOutputStream cos = new CipherOutputStream(fos, cipher);  
                int b;  
                byte[] d = new byte[1024];  
                while ((b = fis.read(d)) != -1) {  
                    cos.write(d, 0, b);  
                }  
                cos.flush();  
                cos.close();  
                fis.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
      
        }  
      
        /**  
         * 使用AES标准解密  
         */  
        public void aesDecrypt() {  
            try {  
                FileInputStream fis = null;  
                fis = new FileInputStream(outPath);  
                ByteArrayOutputStream out = new ByteArrayOutputStream(1024);  
                SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),  
                        "AES");  
                Cipher cipher = Cipher.getInstance("AES");  
                cipher.init(Cipher.DECRYPT_MODE, sks);  
                //CipherInputStream 为加密输入流  
                CipherInputStream cis = new CipherInputStream(fis, cipher);  
                int b;  
                byte[] d = new byte[1024];  
                while ((b = cis.read(d)) != -1) {  
                    out.write(d, 0, b);  
                }  
                out.flush();  
                out.close();  
                cis.close();  
                //获取字节流显示图片  
                byte[] bytes= out.toByteArray();  
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);  
                img.setImageBitmap(bitmap);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
      
        }  
      
    }  
