(ns com.example.components.parser
  (:require
    [com.example.components.auto-resolvers :refer [automatic-resolvers]]
    [com.example.components.blob-store :as bs]
    [com.example.components.config :refer [config]]
    [com.example.components.datomic :refer [datomic-connections]]
    [com.example.components.delete-middleware :as delete]
    [com.example.components.save-middleware :as save]
    [com.example.model :refer [all-attributes]]
    [com.example.model.account :as account]
    [com.example.model.invoice :as invoice]
    [com.example.model.timezone :as timezone]
    [com.fulcrologic.rad.attributes :as attr]
    [com.fulcrologic.rad.blob :as blob]
    [com.fulcrologic.rad.database-adapters.datomic :as datomic]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.pathom :as pathom]
    [mount.core :refer [defstate]]
    [com.example.model.sales :as sales]
    [com.example.model.item :as item]))

(defstate parser
  :start
  (pathom/new-parser config
    [(attr/pathom-plugin all-attributes)
     (form/pathom-plugin save/middleware delete/middleware)
     (datomic/pathom-plugin (fn [env] {:production (:main datomic-connections)}))
     (blob/pathom-plugin bs/temporary-blob-store {:files         bs/file-blob-store
                                                  :avatar-images bs/image-blob-store})]
    [automatic-resolvers
     form/resolvers
     (blob/resolvers all-attributes)
     account/resolvers
     invoice/resolvers
     item/resolvers
     sales/resolvers
     timezone/resolvers]))
