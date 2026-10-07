import {authorizedApiClient} from "./client.ts";

export const statuses = async () =>
    authorizedApiClient.get("account/statuses");