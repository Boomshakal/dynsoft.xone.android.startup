package dynsoft.xone.android.retrofit.deletecloud;

import java.util.ArrayList;

public class IotDeviceBean {

    private ArrayList<String> filter;
    private QueryBean query;

    public IotDeviceBean(ArrayList<String> filter, QueryBean query) {
        this.filter = filter;
        this.query = query;
    }

    /**
     * "offset": "�����б��ƫ����",
     * "limit": "��������",
     * "filter": [
     *         "�ֶ�A",
     *         "�ֶ�B"
     *     ],
     *     "query": {
     *         "filed1": {
     *             "$like": "�ֶ�ֵ"
     *         },
     *         "filed3": {
     *             "$lt": "�ֶ�ֵ"
     *         }
     *     },
     *     "order": {
     *         "filed1": "desc",
     *         "filed2": "asc"
     *     }
     */

    public static class FilterBean {
        private String sn;
        private String name;
        private String mac;
        private int id;

        public FilterBean(String sn, String name, String mac, int id) {
            this.sn = sn;
            this.name = name;
            this.mac = mac;
            this.id = id;
        }
    }

    public static class QueryBean {
        private Filed1Bean sn;

        public QueryBean(Filed1Bean filed1) {
            this.sn = filed1;
        }

        public static class Filed1Bean {
            private String $like;

            public Filed1Bean(String $like) {
                this.$like = $like;
            }
        }
    }

}
