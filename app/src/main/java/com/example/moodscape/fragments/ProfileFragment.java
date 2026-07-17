package com.example.moodscape.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.example.moodscape.R;
import com.example.moodscape.models.User;
import com.example.moodscape.utils.DatabaseHelper;
import com.example.moodscape.utils.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bumptech.glide.Glide;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfileFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;

    CircleImageView ivProfile;
    TextView tvProfileName, tvProfilePhone, tvProfileGender, tvProfileAge, tvProfileScore;
    DatabaseHelper db;
    SessionManager session;
    Uri photoUri;
    String currentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        db = new DatabaseHelper(requireContext());
        session = new SessionManager(requireContext());

        ivProfile = view.findViewById(R.id.ivProfilePic);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfilePhone = view.findViewById(R.id.tvProfilePhone);
        tvProfileGender = view.findViewById(R.id.tvProfileGender);
        tvProfileAge = view.findViewById(R.id.tvProfileAge);
        tvProfileScore = view.findViewById(R.id.tvProfileScore);

        loadUserData();

        view.findViewById(R.id.btnCamera).setOnClickListener(v -> {
            if (checkPermissions()) {
                openCamera();
            } else {
                requestPermissions();
            }
        });
        view.findViewById(R.id.btnGallery).setOnClickListener(v -> {
            if (checkPermissions()) {
                openGallery();
            } else {
                requestPermissions();
            }
        });

        return view;
    }

    private boolean checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                   ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                   ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void loadUserData() {
        User user = db.getUserByPhone(session.getUserPhone());
        if (user != null) {
            tvProfileName.setText("👤 " + user.getName());
            tvProfilePhone.setText("📱 " + user.getPhone());
            tvProfileGender.setText("⚥ " + user.getGender());
            tvProfileAge.setText("🎂 Age: " + user.getAge());
            tvProfileScore.setText("🏆 Mood Score: " + session.getMoodScore() + "/1000");
            if (user.getProfilePic() != null && !user.getProfilePic().isEmpty()) {
                Glide.with(this)
                        .load(user.getProfilePic())
                        .placeholder(R.drawable.ic_profile)
                        .into(ivProfile);
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(requireContext(),
                            requireContext().getPackageName() + ".fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    private File createImageFile() throws IOException {
        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File dir = requireContext().getExternalFilesDir("Pictures");
        File image = File.createTempFile("JPEG_" + stamp, ".jpg", dir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Glide.with(this).load(currentPhotoPath).into(ivProfile);
                db.updateProfilePic(session.getUserPhone(), currentPhotoPath);
            } else if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                Uri selectedImage = data.getData();
                String path = copyImageToInternalStorage(selectedImage);
                if (path != null) {
                    Glide.with(this).load(path).into(ivProfile);
                    db.updateProfilePic(session.getUserPhone(), path);
                }
            }
        }
    }

    private String copyImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File photoFile = createImageFile();
            OutputStream outputStream = new FileOutputStream(photoFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return photoFile.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }
}

