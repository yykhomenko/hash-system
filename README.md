# hash-system

Example:

380670000001 -> fa769a2d564e384aa3867bc23d6a9cd1

fa769a2d564e384aa3867bc23d6a9cd1 -> 380670000001


Results:
- 135000 HTTP requests per second per node(current 270000 by 2 nodes);
- easy to vertical scaling(async server model);
- easy to horizontal scaling(nodes works independently);
- REST interface for clients;
- responsive, resilient, elastic and message driven solution.

This service deployed on 2VMs(4cores Xeon e5 2699 per VM, 32GB RAM, 100GB HDD) and has 1 application and 1 DB per node. It stores 150 000 000 hashes in memory. The service handle 270000 HTTP requests per second. It easy to vertical scaling because it uses fully asynchronous model of computing. It also easy to horizontal scaling because builds as independent nodes. When I design that service I was inspired The Reactive Manifesto.
