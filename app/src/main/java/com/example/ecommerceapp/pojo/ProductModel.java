package com.example.ecommerceapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Comparator;

public class ProductModel implements Parcelable {

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("price")
    private Double price;
    @SerializedName("offer")
    private Float offer;
    @SerializedName("imagePath")
    private String imagePath;
    @SerializedName("descriptionPath")
    private String descriptionPath;
    @SerializedName("specificationPath")
    private String specificationPath;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("brandId")
    private String brandId;
    @SerializedName("subCategoryId")
    private String subCategoryId;
    @SerializedName("available")
    private int available;

    @SerializedName("reviewsCount")
    private int reviewsCount;
    @SerializedName("reviewsRateAverage")
    private float reviewsRateAverage;

    @SerializedName("imagesPaths")
    private ArrayList<ImageProductModel> imagesPaths;


    public ProductModel() {
    }


    protected ProductModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        if (in.readByte() == 0) {
            offer = null;
        } else {
            offer = in.readFloat();
        }
        imagePath = in.readString();
        descriptionPath = in.readString();
        specificationPath = in.readString();
        quantity = in.readInt();
        brandId = in.readString();
        subCategoryId = in.readString();
        available = in.readInt();
        reviewsCount = in.readInt();
        reviewsRateAverage = in.readFloat();
//        imagesPaths = (ArrayList<ImageProductModel>) in.readSerializable();
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Float getOffer() {
        return offer;
    }

    public void setOffer(Float offer) {
        this.offer = offer;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescriptionPath() {
        return descriptionPath;
    }

    public void setDescriptionPath(String descriptionPath) {
        this.descriptionPath = descriptionPath;
    }

    public String getSpecificationPath() {
        return specificationPath;
    }

    public void setSpecificationPath(String specificationPath) {
        this.specificationPath = specificationPath;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public float getReviewsRateAverage() {
        return reviewsRateAverage;
    }

    public void setReviewsRateAverage(float reviewsRateAverage) {
        this.reviewsRateAverage = reviewsRateAverage;
    }

    public ArrayList<ImageProductModel> getImagesPaths() {
        return imagesPaths;
    }

    public void setImagesPaths(ArrayList<ImageProductModel> imagesPaths) {
        this.imagesPaths = imagesPaths;
    }

    public static Comparator<ProductModel> bestMatchesComparator = new Comparator<ProductModel>() {
        @Override
        public int compare(ProductModel m1, ProductModel m2) {
            String p1 = m1.getId();
            String p2 = m2.getId();

            return p1.compareTo(p2);
        }
    };

    public static Comparator<ProductModel> priceLowComparator = new Comparator<ProductModel>() {
        @Override
        public int compare(ProductModel m1, ProductModel m2) {
            Double price1 = m1.getPrice() * ((100 - m1.getOffer())/100);
            Double price2 = m2.getPrice() * ((100 - m2.getOffer())/100);

            return price1.compareTo(price2);
        }
    };
    public static Comparator<ProductModel> priceHighComparator = new Comparator<ProductModel>() {
        @Override
        public int compare(ProductModel m1, ProductModel m2) {
            Double price1 = m1.getPrice() * ((100 - m1.getOffer())/100);
            Double price2 = m2.getPrice() * ((100 - m2.getOffer())/100);

            return price2.compareTo(price1);
        }
    };
    public static Comparator<ProductModel> offerLowComparator = new Comparator<ProductModel>() {
        @Override
        public int compare(ProductModel m1, ProductModel m2) {
            String offer1 = String.valueOf(m1.getOffer());
            String offer2 = String.valueOf(m2.getOffer());

            return offer1.compareTo(offer2);
        }
    };
    public static Comparator<ProductModel> offerHighComparator = new Comparator<ProductModel>() {
        @Override
        public int compare(ProductModel m1, ProductModel m2) {
            String offer1 = String.valueOf(m1.getOffer());
            String offer2 = String.valueOf(m2.getOffer());

            return offer2.compareTo(offer1);
        }
    };
    public static Comparator<ProductModel> topRatedComparator = new Comparator<ProductModel>() {
        @Override
        public int compare(ProductModel m1, ProductModel m2) {
            String rate1 = String.valueOf(m1.getReviewsRateAverage());
            String rate2 = String.valueOf(m2.getReviewsRateAverage());

            return rate2.compareTo(rate1);
        }
    };
    public static Comparator<ProductModel> newComparator = new Comparator<ProductModel>() {
        @Override
        public int compare(ProductModel m1, ProductModel m2) {
            String p1 = m1.getId();
            String p2 = m2.getId();

            return p2.compareTo(p1);
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(price);
        }
        if (offer == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(offer);
        }
        parcel.writeString(imagePath);
        parcel.writeString(descriptionPath);
        parcel.writeString(specificationPath);
        parcel.writeInt(quantity);
        parcel.writeString(brandId);
        parcel.writeString(subCategoryId);
        parcel.writeInt(available);
        parcel.writeInt(reviewsCount);
        parcel.writeFloat(reviewsRateAverage);
//        parcel.writeSerializable(imagesPaths);
    }
}
// category / price range / brand /