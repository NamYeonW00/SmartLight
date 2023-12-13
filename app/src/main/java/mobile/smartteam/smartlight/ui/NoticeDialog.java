package mobile.smartteam.smartlight.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import mobile.smartteam.smartlight.databinding.DialogNoticeBinding;

public class NoticeDialog extends Dialog {
    private DialogNoticeBinding binding;
    private Consumer<DialogInterface> onPositiveListener;
    private Consumer<DialogInterface> onNegativeListener;

    protected NoticeDialog(@NonNull Context context) {
        super(context);
        init();
    }

    protected NoticeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected NoticeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        binding = DialogNoticeBinding.inflate(LayoutInflater.from(getContext()));
        binding.positiveButton.setOnClickListener(v -> {
            if (onPositiveListener != null) {
                onPositiveListener.accept(this);
            }

            this.dismiss();
        });

        binding.negativeButton.setOnClickListener(v -> {
            if (onNegativeListener != null) {
                onNegativeListener.accept(this);
            }

            this.dismiss();
        });

        setContentView(binding.getRoot());
    }

    public NoticeDialog setOnPositiveListener(Consumer<DialogInterface> onPositiveListener) {
        this.onPositiveListener = onPositiveListener;
        return this;
    }

    public NoticeDialog setOnNegativeListener(Consumer<DialogInterface> onNegativeListener) {
        this.onNegativeListener = onNegativeListener;
        return this;
    }

    public NoticeDialog setMessage(String message) {
        binding.messageTextView.setText(message);
        return this;
    }

    @Override
    public void show() {
        super.show();

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
