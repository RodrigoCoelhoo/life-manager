import { useEffect, useState } from "react";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import type { PageResponseDTO } from "../../services/api.dto";
import { transferenceService } from "../../services/finances/transference/transference.service";
import React from "react";
import { Pagination } from "../../components/common/Pagination";
import { PencilSquareIcon } from "@heroicons/react/16/solid";
import { Modal } from "../../components/common/Modal";
import toast from "react-hot-toast";
import type { TransferenceDTO, TransferenceResponseDTO } from "../../services/finances/transference/transference.dto";
import TransferenceForm from "../../components/finances/TransferenceForm";

export default function Transferences() {
	const [isOpen, setIsOpen] = useState<boolean>(false);
	const [transferences, setTransferences] = useState<TransferenceResponseDTO[]>([]);
	const [activeTransference, setActiveTransference] = useState<TransferenceResponseDTO>();
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const elementsPerPage = 24;

	const fetchTransferences = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: PageResponseDTO<TransferenceResponseDTO> = await transferenceService.getAllTransferences(page - 1, elementsPerPage);

			setTransferences(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch exercises");
		} finally {
			setLoading(false);
		}
	};

	const createTransference = async (transference: TransferenceDTO) => {
		try {
			setLoading(true);
			const data: TransferenceResponseDTO = await transferenceService.createTransference(transference);

			setTransferences(prev => {
				const updated = [data, ...prev].sort((a, b) => {
					const timeA = new Date(a.date).getTime();
					const timeB = new Date(b.date).getTime();

					if (timeA !== timeB) return timeB - timeA;
					return b.id - a.id;
				});

				return updated.slice(0, elementsPerPage);
			});

			setTotalElements(prev => prev + 1);
		} catch (err) {
			console.error(err);
			toast.error("Wallet balance is insufficient!");
		} finally {
			setLoading(false);
		}
	};

	const updateTransference = async (id: number, transference: TransferenceDTO) => {
		try {
			setLoading(true);
			const updated = await transferenceService.updateTransference(id, transference);
			setTransferences(prev => prev.map(e => (e.id === id ? updated : e)));
		} catch (err) {
			toast.error("Wallet balance is insufficient!");
		} finally {
			setLoading(false);
		}
	};

	const deleteTransference = async (id: number) => {
		try {
			setLoading(true);
			await transferenceService.deleteTransference(id);
			fetchTransferences();
			setTotalElements(prev => prev - 1);
		} catch (err) {
			toast.error("Wallet balance can't be bellow 0.00");
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		fetchTransferences();
	}, [page]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load transferences"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchTransferences()}
				/>
			</div>
		)
	};

	return (
		<>
			<div className="w-full p-2 sm:p-6 text-textcolor flex flex-col gap-4">

				<div className="flex items-end justify-between gap-4 px-2">
					<button
						className="bg-primary w-fit p-2 px-4 rounded-xl cursor-pointer hover:bg-primary/80 font-semibold"
						onClick={() => setIsOpen(true)}
					>
						Create +
					</button>

					<h1 className="text-3xl text-red-400">Filters missing, by Wallets and by Date between</h1>

					<span className="text-secondary"> {totalElements} {totalElements === 1 ? "transference" : "transferences"} </span>
				</div>

				<div className="h-full p-2 w-full text-textcolor text-sm rounded-t-lg grid grid-cols-[4fr_6fr_5fr_6fr_5fr_4fr] sm:grid-cols-[4fr_6fr_5fr_6fr_5fr_10fr_4fr] drop-shadow-[0px_4px_6px_rgba(0,0,0,0.2)]">

					{/* Header */}
					<div className="font-semibold bg-primary py-2 px-2 rounded-tl-lg">Date</div>
					<div className="font-semibold bg-primary py-2 px-2 wrap-break-words">Sender</div>
					<div className="font-semibold bg-primary py-2 px-2">Amount</div>
					<div className="font-semibold bg-primary py-2 px-2 wrap-break-words">Receiver</div>
					<div className="font-semibold bg-primary py-2 px-2">Amount</div>
					<div className="font-semibold bg-primary py-2 px-2 hidden sm:block">Description</div>
					<div className="font-semibold bg-primary py-2 px-2 rounded-tr-lg truncate text-center"></div>

					{/* Rows */}
					{transferences.map((item, index) => {
						const rowStyle = index % 2 === 0 ? "bg-foreground/90" : "bg-foreground/70";

						return (
							<React.Fragment key={item.id}>
								<div className={`${rowStyle} py-1 px-2 font-extralight whitespace-nowrap min-w-max`}>{item.date}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight truncate`}>{item.fromWallet.name}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight text-[#F87171]`}>-{item.fromAmount}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight truncate`}>{item.toWallet.name}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight text-[#34D399]`}>+{item.toAmount}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight truncate hidden sm:block`}>
									{item.description || "No description provided"}
								</div>
								<button
									className={`${rowStyle} py-1 px-2 font-extralight`}
									onClick={() => {
										setActiveTransference(item);
										setIsOpen(true);
									}}
								>
									<PencilSquareIcon className="h-5 w-5 hover:rounded-full hover:bg-gray-400/30 p-0.5 cursor-pointer" />
								</button>
							</React.Fragment>
						);
					})}
				</div>

				<div className="mb-4">
					<Pagination
						currentPage={page}
						totalPages={totalPages}
						onPageChange={(p: number) => setPage(p)}
					/>
				</div>
			</div>

			<Modal
				isOpen={isOpen}
				onClose={() => {
					setIsOpen(false);
					setActiveTransference(undefined);
				}}>
				<TransferenceForm
					transference={activeTransference}
					onClose={() => {
						setIsOpen(false);
						setActiveTransference(undefined);
					}}
					onCreate={createTransference}
					onDelete={deleteTransference}
					onUpdate={updateTransference}
				/>
			</Modal>
		</>
	);
}