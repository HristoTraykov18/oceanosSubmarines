package com.htraykov.oceanossubmarines;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class SubmarineFragment extends Fragment {
    private static final String SUBM_FRAGMENT_IDENTIFIER_NAME = "submLayoutIdentifier";
    private static final String SUBM_NAME_STRING = "submName";
    private static final String AIRLOCK_LEVEL = "airlock_level";
    private static final String AQUARIUM_LEVEL = "aquarium_level";
    private static final String COCKPIT_LEVEL = "cockpit_level";
    private static final String MOTOR_LEVEL = "motor_level";
    private static final String PROPELLER_LEVEL = "propeller_level";
    private static String packageName;

    // Proper way to construct an object in android studio
    // if additional parameters are needed is via newInstance
    public static SubmarineFragment newInstance(String submarineName, FragmentActivity parentActivity) {
        Bundle args = new Bundle();
        packageName = parentActivity.getPackageName();

        // Set arguments for onCreateView
        {
            String[] submParts = new String[] {
                    AIRLOCK_LEVEL,
                    AQUARIUM_LEVEL,
                    COCKPIT_LEVEL,
                    MOTOR_LEVEL,
                    PROPELLER_LEVEL,
            };

            for (String submPart : submParts) // Set level 1 to all submarine parts
                args.putInt(submPart, 1);
        }

        args.putString(SUBM_NAME_STRING, submarineName);
        args.putInt(SUBM_FRAGMENT_IDENTIFIER_NAME,
            parentActivity.getResources() // Save the ID of the related initial view (xml file)
                          .getIdentifier(submarineName + "_initial_view",
                                         "layout",
                                         packageName
        ));

        // Create the instance and add the arguments
        SubmarineFragment sf = new SubmarineFragment();
        sf.setArguments(args);
        return sf;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

        // Create the requested submarine view and add on click listeners to all submarine parts
        if (args != null && getActivity() != null) {
            // Submarine parts are always the same 5
            Activity activity = getActivity();
            String[] submParts = new String[] {
                this.getString(R.string.subm_part_airlock),
                this.getString(R.string.subm_part_aquarium),
                this.getString(R.string.subm_part_cockpit),
                this.getString(R.string.subm_part_motor),
                this.getString(R.string.subm_part_propeller)
            };
            View view = inflater.inflate(args.getInt(SUBM_FRAGMENT_IDENTIFIER_NAME),
                                    container,
                                    false);
            String submName = args.getString(SUBM_NAME_STRING);
            Resources resources = LocaleHelper.setLocale(activity, MainActivity.currentLangCode);

            for (String submPart : submParts) {
                view.findViewById(resources.getIdentifier(
                        submName + submPart, // Id of each submarine part
                        "id",
                        packageName
                )).setOnClickListener(v -> {

                    // Create new popup with the specified xml file as layout
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    View popupView = inflater.inflate(R.layout.edit_subm_part_popup,
                        activity.findViewById(R.id.editSubmPartPopup));

                    String editedPartKey = submPart.toLowerCase() + "_level";

                    // Set submarine part image when the popup appears
                    popupView.findViewById(R.id.editSubmPartPopupImage).setBackground(
                        resources.getDrawable(
                            resources.getIdentifier( // Get the identifier for the image to be set
                                submName + "_" + editedPartKey + "_" + args.getInt(editedPartKey),
                                "drawable",
                                packageName
                    )));

                    updatePopupTextAndPositioning(popupView, resources, submPart);

                    AlertDialog popup = builder.setView(popupView).show();

                    configurePopupArrowButtons(activity, popup, popupView, resources,
                        args, submName, submPart, editedPartKey, args.getInt(editedPartKey),
                        popupView.findViewById(R.id.upgradeArrow),
                        popupView.findViewById(R.id.downgradeArrow));
                });
            }

            return view;
        }

        return null;
    }

    private void configurePopupArrowButtons(Activity activity, AlertDialog popup, View popupView,
                                            Resources resources, Bundle args, String submName,
                                            String editedPart, String editedPartKey, int editedPartLevel,
                                            ImageButton upgradeButton, ImageButton downgradeButton) {
        // Downgrade button logic
        if (editedPartLevel > 1) {
            downgradeButton.setOnClickListener(v -> { // Downgrade button event listener
                // Update the new level of the part
                args.putInt(editedPartKey, editedPartLevel - 1);
                setArguments(args);

                // Change the background of the image button in SubmEditActivity
                activity.findViewById(resources.getIdentifier(
                    // Example for name is grabberAirlock
                    submName + editedPart,
                    "id",
                    packageName
                )).setBackground(resources.getDrawable(resources.getIdentifier(
                    // Example for name is grabber_airlock_level_1
                    args.getString(SUBM_NAME_STRING) + "_" + editedPartKey + "_" + (editedPartLevel - 1),
                    "drawable",
                    packageName
                )));

                popup.dismiss(); // Close the popup
            });
        }
        else {
            // 1 is the minimum level for each part,
            // change the background of the downgrade button and show MIN text
            popupView.findViewById(R.id.editSubmPartPopupDowngradeHint)
                .setVisibility(View.VISIBLE); // Show the hint text
            downgradeButton.setBackground(resources.getDrawable(R.drawable.downgrade_arrow_disabled));
        }

        // Upgrade button logic
        if (editedPartLevel < 3) {
            upgradeButton.setOnClickListener(v -> { // Upgrade button event listener
                // Update the new level of the part
                args.putInt(editedPartKey, editedPartLevel + 1);
                setArguments(args);

                // Change the background of the image button in SubmEditActivity
                activity.findViewById(resources.getIdentifier(
                    // Example for name is grabberAirlock
                    submName + editedPart,
                    "id",
                    packageName
                )).setBackground(resources.getDrawable(resources.getIdentifier(
                    // Example for name is grabber_airlock_level_1
                    args.getString(SUBM_NAME_STRING) + "_" + editedPartKey + "_" + (editedPartLevel + 1),
                    "drawable",
                    packageName
                )));

                popup.dismiss(); // Close the popup
            });
        }
        else {
            // 3 is the maximum level for each part,
            // change the background of the upgrade button and show MAX text
            popupView.findViewById(R.id.editSubmPartPopupUpgradeHint)
                .setVisibility(View.VISIBLE); // Show the hint text
            upgradeButton.setBackground(resources.getDrawable(R.drawable.upgrade_arrow_disabled));
        }
    }

    private void updatePopupTextAndPositioning(View popupView, Resources resources, String submarinePart) {
        int idOfTitleText;
        int idOfDescText;

        // Get proper text description ID depending on the edited submarine part
        if (submarinePart.equals(resources.getString(R.string.subm_part_airlock))) {
            idOfTitleText = R.string.airlock;
            idOfDescText = R.string.airlock_desc;
        }
        else if (submarinePart.equals(resources.getString(R.string.subm_part_aquarium))) {
            idOfTitleText = R.string.aquarium;
            idOfDescText = R.string.aquarium_desc;
        }
        else if (submarinePart.equals(resources.getString(R.string.subm_part_cockpit))) {
            idOfTitleText = R.string.cockpit;
            idOfDescText = R.string.cockpit_desc;
        }
        else if (submarinePart.equals(resources.getString(R.string.subm_part_motor))) {
            idOfTitleText = R.string.motor;
            idOfDescText = R.string.motor_desc;

            // In case if the edited part is the motor,
            // the image width has to be slightly changed
            // getDisplayMetrics().density is used to convert properly from dp to px
            popupView.findViewById(R.id.editSubmPartPopupImage) // Set width to 150dp
                 .getLayoutParams()
                 .width = (int)(150 * resources.getDisplayMetrics().density + 0.5f);
        }
        else if (submarinePart.equals(resources.getString(R.string.subm_part_propeller))) {
            idOfTitleText = R.string.propeller;
            idOfDescText = R.string.propeller_desc;

            // In case if the edited part is the propeller,
            // the image height has to be slightly changed
            // getDisplayMetrics().density is used to convert properly from dp to px
            popupView.findViewById(R.id.editSubmPartPopupImage) // Set height to 170dp
                 .getLayoutParams()
                 .height = (int)(170 * resources.getDisplayMetrics().density + 0.5f);
        }
        else {
            idOfTitleText = R.string.error_finding_part_name_desc;
            idOfDescText = R.string.error_finding_part_name_desc;
        }

        String titleText = resources.getString(R.string.edit) + " " + resources.getString(idOfTitleText);

        ((TextView)popupView.findViewById(R.id.editSubmPartPopupTitle)) // Set title text
            .setText(titleText);
        ((TextView)popupView.findViewById(R.id.editSubmPartPopupDesc)) // Set description text
            .setText(resources.getString(idOfDescText));
    }
}
