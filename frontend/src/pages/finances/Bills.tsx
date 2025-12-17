import { useEffect, useState } from "react";
import type { PageResponseDTO } from "../../services/api.dto";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import { Modal } from "../../components/common/Modal";
import { Pagination } from "../../components/common/Pagination";
import { automaticTransactionService } from "../../services/finances/automatic-transactions/automatic-transactions.service";
import type { AutomaticTransactionDTO, AutomaticTransactionResponseDTO } from "../../services/finances/automatic-transactions/automatic-transactions.dto";
import BillForm from "../../components/finances/BillForm";
import BillCard from "../../components/finances/BillCard";
import { FaCircleInfo } from "react-icons/fa6";

export default function Bills() {
	const [bills, setBills] = useState<AutomaticTransactionResponseDTO[]>([]);
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const [elementsPerPage, setElementsPerPage] = useState<number>(18);

	const [createBillOpen, setCreateBillOpen] = useState<boolean>(false);

	const fetchBills = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: PageResponseDTO<AutomaticTransactionResponseDTO> = await automaticTransactionService.getAutomaticTransactions(
				page - 1, elementsPerPage
			);

			setBills(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch bills");
		} finally {
			setLoading(false);
		}
	};

	const createBill = async (bill: AutomaticTransactionDTO) => {
		try {
			setLoading(true);
			const data: AutomaticTransactionResponseDTO = await automaticTransactionService.createAutomaticTransaction(bill);
			setBills(prev => {
				const updated = [data, ...prev];

				if (updated.length > elementsPerPage) {
					updated.pop();
				}

				return updated;
			});

			setTotalElements(prev => prev + 1);
		} catch (err) {
			console.error(err);
			setError("Failed to create a bill");
		} finally {
			setLoading(false);
		}
	};

	const updateBill = async (id: number, updatedData: AutomaticTransactionDTO) => {
		try {
			setLoading(true);
			const updated = await automaticTransactionService.updateAutomaticTransaction(id, updatedData);
			setBills(prev => prev.map(e => (e.id === id ? updated : e)));
		} catch (err) {
			setError("Failed to update bill");
		} finally {
			setLoading(false);
		}
	};

	const deleteBill = async (id: number) => {
		try {
			setLoading(true);
			await automaticTransactionService.deleteAutomaticTransaction(id);
			fetchBills();
			setTotalElements(prev => prev - 1);
		} catch (err) {
			setError("Failed to delete bill");
		} finally {
			setLoading(false);
		}
	};

	const triggerBill = async (id: number) => {
		try {
			setLoading(true);
			await automaticTransactionService.triggerAutomaticTransaction(id);
			fetchBills();
		} catch (err) {
			setError("Failed to delete bill");
		} finally {
			setLoading(false);
		}
	}


	useEffect(() => {
		fetchBills();
	}, [page, elementsPerPage]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load bills"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchBills()}
				/>
			</div>
		)
	};

	return (
		<>
			<div className="w-full p-6 text-textcolor flex flex-col gap-4">

				<div className="flex items-center justify-between gap-4">
					<button
						className="bg-primary p-2 px-4 rounded-xl cursor-pointer hover:bg-primary/80 font-semibold"
						onClick={() => setCreateBillOpen(true)}
					>
						Create +
					</button>

					<div className="flex items-center gap-3">
						<div className="flex gap-3 items-center">
							<label htmlFor="billsPerPage" className="text-sm mb-1 font-extralight">
								Bills per page
							</label>

							<select
								id="billsPerPage"
								name="billsPerPage"
								required
								className="form-input w-14"
								value={elementsPerPage}
								onChange={(e) => setElementsPerPage(Number(e.target.value))}
							>
								<option value="12">12</option>
								<option value="18">18</option>
								<option value="24">24</option>
							</select>
						</div>

						<span className="text-secondary"> {totalElements} {totalElements === 1 ? "bill" : "bills"} </span>
					</div>
				</div>

				<div className="flex flex-row items-center ml-2 gap-2 text-textcolor/80">
					<FaCircleInfo size={12} />
					<span className="font-extralight text-sm">
						Bills are reviewed daily at 00:00 UTC and processed automatically on their scheduled due dates.
					</span>
				</div>

				<div className="flex flex-col gap-2">
					<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
						{bills.map((bill) => (
							<BillCard
								key={bill.id}
								{...bill}
								onUpdate={updateBill}
								onDelete={deleteBill}
								onTrigger={triggerBill}
							/>
						))}
					</div>
				</div>

				<div className="mb-4">
					<Pagination
						currentPage={page}
						totalPages={totalPages}
						onPageChange={(p: number) => setPage(p)}
					/>
				</div>
			</div>

			<Modal isOpen={createBillOpen} onClose={() => setCreateBillOpen(false)}>
				<BillForm
					onClose={() => setCreateBillOpen(false)}
					onCreate={createBill}
				/>
			</Modal>
		</>
	);
}
