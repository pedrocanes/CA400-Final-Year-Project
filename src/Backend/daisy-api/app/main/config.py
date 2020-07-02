import os

postgres_prod_base = "postgresql://postgres:post-DAISY-123@daisy-db.c3z0tnbgjfls.us-west-2.rds.amazonaws.com/daisy_db"
postgres_test_base = "postgresql://postgres:post-DAISY-123@daisy-db.c3z0tnbgjfls.us-west-2.rds.amazonaws.com/test_db"

class DevelopmentConfig(Config):
    DEBUG = True
    SQLALCHEMY_DATABASE_URI = postgres_prod_base
    SQLALCHEMY_TRACK_MODIFICATIONS = False

class TestingConfig(Config):
    DEBUG = True
    TESTING = True
    SQLALCHEMY_DATABASE_URI = postgres_test_base
    PRESERVE_CONTEXT_ON_EXCEPTION = False
    SQLALCHEMY_TRACK_MODIFICATIONS = False

class ProductionConfig(Config):
    DEBUG = True
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_DATABASE_URI = postgres_prod_base

config_by_name = dict(
    dev=DevelopmentConfig,
    test=TestingConfig,
    prod=ProductionConfig
)
