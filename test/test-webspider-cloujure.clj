(import '(com.knowledgebooks.info_spiders WebSpider))

(defn get-pages [starting-url max-pages]
  (let [ws (new WebSpider starting-url max-pages)]
    (map seq (.url_content_lists ws))))

(println (get-pages "http://www.knowledgebooks.com" 2))

