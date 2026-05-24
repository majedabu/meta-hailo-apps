# meta-hailo-apps

Yocto meta layer for building Hailo C++ inference applications on Astrial (NXP i.MX8MP) with Scarthgap.

## Applications included

| App | Description |
|---|---|
| `object_detection` | Generic object detection |
| `instance_segmentation` | Instance segmentation with masks |
| `classification` | Image classification |
| `semantic_segmentation` | Per-pixel scene segmentation |
| `pose_estimation` | Human pose estimation |
| `oriented_object_detection` | Object detection with rotation angles |
| `depth_estimation_mono` | Monocular depth estimation |
| `depth_estimation_stereo` | Stereo depth estimation |
| `onnxrt_hailo_pipeline` | Hailo inference + ONNX Runtime postprocessing |
| `zero_shot_classification` | Open-vocabulary classification without retraining |

## Adding to your manifest

Add the following line to your `astrial-6.6.52.xml` manifest (or equivalent):

```xml
<remote fetch="https://github.com/majedabu" name="majedabu"/>

<project name="meta-hailo-apps" remote="majedabu" revision="main" path="sources-extra/meta-hailo-apps"/>
```

## Adding to your image

In your image recipe or `local.conf`:

```bitbake
IMAGE_INSTALL += "hailo-cpp-apps"
```

## Layer dependencies

- `meta-oe` (openembedded-layer)
- `meta-hailo-imx` — provides `libhailort`

## Build output

Binaries are installed to `/hailo-apps/<app-name>/` on the target.
