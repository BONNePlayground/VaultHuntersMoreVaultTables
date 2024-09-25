//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.morevaulttables.config;


import net.minecraftforge.common.ForgeConfigSpec;


/**
 * The configuration handling class. Holds all the config values.
 */
public class Configuration
{
    /**
     * The constructor for the config.
     */
    public Configuration()
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        this.blockVaultDolls = builder.
            comment("This option allows to toggle if Vault Dolls should be able to place in the world.",
                "By default: false - does not blocks vault doll placement").
            define("block-vault-dolls", false);

        this.dollDismantlerEnergyStorage = builder.
            comment("This option allows to change the amount of energy that Doll Dismantler stores inside block.",
                "By default: 1000").
            defineInRange("doll-dismantler-energy-storage", 1000, 0, Integer.MAX_VALUE);

        this.dollDismantlerEnergyConsumption = builder.
            comment("This option allows to change the amount of energy that Doll Dismantler uses per tick.",
                "By default: 16").
            defineInRange("doll-dismantler-energy-consumption", 16, 0, Integer.MAX_VALUE);

        this.dollDismantlerEnergyTransfer = builder.
            comment("This option allows to change the amount of energy that Doll Dismantler can receive/extract.",
                "By default: 100").
            defineInRange("doll-dismantler-energy-transfer", 100, 0, Integer.MAX_VALUE);

        this.dollDismantlerExtractionSpeed = builder.
            comment("This option allows to change the extraction speed for doll dismantler.",
                "The extraction speed is defined by `stack per ticks`.",
                "By default: 5").
            defineInRange("doll-dismantler-extraction-speed", 5, 1, 100);

        this.dollDismantlerVolume = builder.
            comment("This option allows to change the sound for Doll Dismantler operation.",
                "By default: 0.5").
            defineInRange("doll-dismantler-volume", 0.5f, 0, 1f);

        this.dollDismantlerRotationSpeed = builder.
            comment("This option allows to change the rotation speed for Doll inside Dismantler.",
                "By default: 25").
            defineInRange("doll-dismantler-rotation-speed", 25d, 0, 2000);

        Configuration.GENERAL_SPEC = builder.build();
    }


    /**
     * Gets block vault dolls.
     *
     * @return the block vault dolls
     */
    public boolean getBlockVaultDolls()
    {
        return this.blockVaultDolls.get();
    }


    /**
     * Gets doll dismantler energy storage.
     *
     * @return the doll dismantler energy storage
     */
    public int getDollDismantlerEnergyStorage()
    {
        return this.dollDismantlerEnergyStorage.get();
    }


    /**
     * Gets doll dismantler energy consumption.
     *
     * @return the doll dismantler energy consumption
     */
    public int getDollDismantlerEnergyConsumption()
    {
        return this.dollDismantlerEnergyConsumption.get();
    }


    /**
     * Gets doll dismantler energy transfer.
     *
     * @return the doll dismantler energy transfer
     */
    public int getDollDismantlerEnergyTransfer()
    {
        return this.dollDismantlerEnergyTransfer.get();
    }


    /**
     * Gets doll dismantler volume.
     *
     * @return the doll dismantler volume
     */
    public double getDollDismantlerVolume()
    {
        return this.dollDismantlerVolume.get();
    }


    /**
     * Gets doll dismantler rotation speed.
     *
     * @return the doll dismantler rotation speed
     */
    public double getDollDismantlerRotationSpeed()
    {
        return this.dollDismantlerRotationSpeed.get();
    }


    /**
     * Gets doll dismantler extraction speed in ticks.
     *
     * @return the doll dismantler extraction speed in ticks
     */
    public int getDollDismantlerExtractionSpeed()
    {
        return this.dollDismantlerExtractionSpeed.get();
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------


    /**
     * Stores config variable value.
     */
    private final ForgeConfigSpec.BooleanValue blockVaultDolls;

    /**
     * Stores config variable value.
     */
    private final ForgeConfigSpec.IntValue dollDismantlerEnergyStorage;

    /**
     * Stores config variable value.
     */
    private final ForgeConfigSpec.IntValue dollDismantlerEnergyConsumption;

    /**
     * Stores config variable value.
     */
    private final ForgeConfigSpec.IntValue dollDismantlerEnergyTransfer;

    /**
     * Stores config variable value.
     */
    private final ForgeConfigSpec.DoubleValue dollDismantlerVolume;

    /**
     * Stores config variable value.
     */
    private final ForgeConfigSpec.DoubleValue dollDismantlerRotationSpeed;

    /**
     * Stores config variable value.
     */
    private final ForgeConfigSpec.IntValue dollDismantlerExtractionSpeed;

    /**
     * The general config spec.
     */
    public static ForgeConfigSpec GENERAL_SPEC;
}
